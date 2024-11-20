package top.chukongxiang.mybatis.basemapper.providers;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.chukongxiang.mybatis.basemapper.model.annnotations.TableField;
import top.chukongxiang.mybatis.basemapper.model.annnotations.TableId;
import top.chukongxiang.mybatis.basemapper.model.annnotations.TableName;
import top.chukongxiang.mybatis.basemapper.model.enums.DbType;
import top.chukongxiang.mybatis.basemapper.model.enums.WrapType;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author chukongxiang
 */
@Data
@Slf4j
public final class TableMetadata<T> implements ApplicationContextAware {

    private static final Map<Class<?>, TableMetadata<?>> ENTITY_METADATA_CACHE = new LinkedHashMap<>();

    private final Class<T> entityClass;

    // 这里应该只读
    static DbType DB_TYPE = dbType("com.mysql.cj.jdbc.Driver");

    private String tableName;

    /**
     * id列
     */
    private List<Field> idFields = new ArrayList<>();

    /**
     * 所有可用的字段
     */
    private List<Field> fields = new ArrayList<>();

    /**
     * 实体字段：包装后的sql字段
     */
    private Map<Field, String> fieldWrappedCoulmnMap = new LinkedHashMap<>();

    /**
     * 实体字段：未包装的sql字段
     */
    private Map<Field, String> fieldColumnMap = new LinkedHashMap<>();

    private TableMetadata(Class<T> entityClass) {

        this.entityClass = entityClass;

        WrapType wrapType = WrapType.AUTO;
        String wrap = "";
        String tableName = entityClass.getSimpleName();

        // 取类注解
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation != null) {
            wrapType = tableNameAnnotation.wrapType();
            wrap = tableNameAnnotation.wrap();
            tableName = tableNameAnnotation.value();
            // TODO 如果注解在父类上则切换entityClass
        }

        this.tableName = wrap(wrapType, wrap, tableName);

        Field[] fields = ReflectUtil.getFields(entityClass);
        for (Field field : fields) {

            // 列名
            Pair<String, String> column = getColumnName(field);
            if (column == null) {
                continue;
            }

            // 放入缓存
            fieldWrappedCoulmnMap.put(field, column.getValue());
            fieldColumnMap.put(field, column.getKey());

            if (AnnotationUtil.hasAnnotation(field, TableId.class)) {
                idFields.add(field);
            }
            this.fields.add(field);
        }

        // TODO 这里应该根据配置来
//        if (CollUtil.isEmpty(idFields)) {
//            // 实体没有注解了@TableId的字段，检查是否有id字段名
//            Field idField = ReflectUtil.getField(entityClass, "id");
//            if (idField != null && fieldWrappedCoulmnMap.containsKey(idField)) {
//                idFields.add(idField);
//            }
//        }
        if (CollUtil.isEmpty(idFields)) {
            log.warn("[Mybatis] 没有检测到表 {} 的主键: {}", tableName, entityClass.getName());
        }
    }

    /**
     * 获取字段的列名
     * @param field 字段
     * @return key:未包装的列名，value:包装后的列名
     */
    private Pair<String, String> getColumnName(Field field) {
        if (field == null) {
            return null;
        }

        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && !tableField.exist()) {
            return null;
        }
        TableId tableId = field.getAnnotation(TableId.class);

        // 默认列名
        String columnName = field.getName();
        boolean customColumn = false;
        if (tableId != null && StrUtil.isNotBlank(tableId.value())) {
            columnName = tableId.value();
            customColumn = true;
        } else if (tableField != null && StrUtil.isNotBlank(tableField.value())) {
            columnName = tableField.value();
            customColumn = true;
        }

        boolean mapUnderlineCase = true;
        WrapType wrapType = WrapType.AUTO;
        String wrap = "";

        if (tableField != null) {
            mapUnderlineCase = tableField.mapUnderlineCase();
            wrapType = tableField.wrapType();
            wrap = tableField.wrap();
        }

        if (mapUnderlineCase && !customColumn) {
            // 如果没配置列名，则使用下划线转换
            columnName = StrUtil.toUnderlineCase(columnName);
        }

        return Pair.of(columnName, wrap(wrapType, wrap, columnName));
    }

    private static String wrap(WrapType wrapType, String wrap, String name) {
        switch (wrapType) {
            case ENABLE: {
                return StrUtil.wrap(name, wrap);
            }
            case AUTO: {
                // 自动识别
                if (DB_TYPE == null) {
                    return name;
                }
                return autoWrap(name);
            }
            case DISABLE:
            default: {
                return name;
            }
        }
    }

    public static String autoWrap(String column) {
        if (DB_TYPE == null) {
            return column;
        }
        switch (DB_TYPE) {
            case mysql:
            case mariadb: {
                return StrUtil.wrap(column, "`");
            }
            case oracle:
            case postgresql: {
                return StrUtil.wrap(column, "\"");
            }
            case sqlserver:
            case sqlite: {
                return "[" + column + "]";
            }
            default: {
                return column;
            }
        }
    }

    /**
     * 获取select时需要查询的字符串
     * @return
     */
    public String getSelectColumn() {
        if (MapUtil.isEmpty(this.fieldWrappedCoulmnMap)) {
            return "*";
        }
        List<String> columns = new ArrayList<>();
        for (Field field : this.fieldWrappedCoulmnMap.keySet()) {
            boolean select = true;
            if (AnnotationUtil.hasAnnotation(field, TableField.class)) {
                select = AnnotationUtil.getAnnotation(field, TableField.class).select();
            }
            if (select) {
                columns.add(this.fieldWrappedCoulmnMap.get(field));
            }
        }
        return String.join(", ", columns);
    }

    public Field lookup(String fieldName) {
        for (Field field :  this.fieldColumnMap.keySet()) {
            if (Objects.equals(field.getName(), fieldName)) {
                return field;
            }
        }
        return null;
    }

    public String lookupColumn(String fieldName) {
        Field field = lookup(fieldName);
        if (field == null) {
            return null;
        }
        return getWrappedColumn(field);
    }

    public String getWrappedColumn(Field field) {
        return MapUtil.getStr(this.fieldWrappedCoulmnMap, field);
    }

    public String getRealColumn(Field field) {
        return MapUtil.getStr(this.fieldColumnMap, field);
    }

    public boolean isIdField(Field field) {
        return getIdFields().contains(field);
    }

    public static <E> TableMetadata<E> forClass(Class<? extends E> entityClass) {
        Class<E> readlEntityClass = findEntityClass(entityClass);
        readlEntityClass = readlEntityClass == null ? (Class<E>) entityClass : readlEntityClass;
        return (TableMetadata<E>) ENTITY_METADATA_CACHE.computeIfAbsent(readlEntityClass, TableMetadata::new);
    }

    private static <E> Class<E> findEntityClass(Class<? extends E> entityClass) {
        TableName tableName = AnnotationUtil.getAnnotation(entityClass, TableName.class);
        if (tableName != null) {
            return (Class<E>) entityClass;
        }
        Class<?> superclass = entityClass.getSuperclass();
        if (superclass == Object.class || superclass == null) {
            return null;
        }
        return (Class<E>) findEntityClass(superclass);
    }

    private static DbType dbType(String driverClassName) {
        return dbType(driverClassName, null);
    }

    private static DbType dbType(String driverClassName, DbType defaultType) {
        if (StrUtil.isBlank(driverClassName)) {
            return defaultType;
        }
        switch (driverClassName) {
            case "org.postgresql.Driver": {
                return DbType.postgresql;
            }
            case "oracle.jdbc.OracleDriver":
            case "oracle.jdbc.driver.OracleDriver": {
                return DbType.oracle;
            }
            case "org.sqlite.JDBC": {
                return DbType.sqlite;
            }
            case "com.microsoft.sqlserver.jdbc.SQLServerDriver": {
                return DbType.sqlserver;
            }
            case "org.mariadb.jdbc.Driver": {
                return DbType.mariadb;
            }
            case "com.mysql.jdbc.Driver":
            case "com.mysql.cj.jdbc.Driver" : {
                return DbType.mysql;
            }
            default: {
                return defaultType;
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DataSourceProperties dataSourceProperties = applicationContext.getBean(DataSourceProperties.class);
        String driverClassName = dataSourceProperties.getDriverClassName();
        DB_TYPE = dbType(driverClassName);
    }
}