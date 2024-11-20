package top.chukongxiang.mybatis.basemapper.providers;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;
import top.chukongxiang.mybatis.basemapper.BaseMapper;
import top.chukongxiang.mybatis.basemapper.model.Constants;
import top.chukongxiang.mybatis.basemapper.model.annnotations.TableField;
import top.chukongxiang.mybatis.basemapper.model.annnotations.TableId;
import top.chukongxiang.mybatis.basemapper.model.enums.DbType;
import top.chukongxiang.mybatis.basemapper.model.enums.FieldStrategy;
import top.chukongxiang.mybatis.basemapper.model.enums.IdType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 19:08:00
 */
public class MapperInsertProvider extends AbstractMapperProvider {

    /**
     * 插入
     * @param providerContext
     * @param entity
     * @return
     * @param <T>
     * @see BaseMapper#insert(Object)
     */
    public <T> String insert(ProviderContext providerContext, Map<String, ?> params) {
        T item = (T) params.get("item");
        if (item == null) {
            throw new IllegalArgumentException("param1 can not be null");
        }
        Class<T> entityClass = (Class<T>) entityClass(providerContext);
        TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
        setTableId(item, tableMetadata);
        return getCachedSql(providerContext, () -> "<script>" + buildInsertXmlCondition(entityClass, Constants.ENTITY) + "</script>");
    }

    /**
     * 插入或更新
     * @param providerContext
     * @param params
     * @return
     * @param <T>
     * @see BaseMapper#insertOrUpdate(Object)
     */
    public <T> String insertOrUpdate(ProviderContext providerContext, Map<String, ?> params) {
        T item = (T) params.get("item");
        Class<T> entityClass = (Class<T>) entityClass(providerContext);
        TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
        setTableId(item, tableMetadata);
        return getCachedSql(providerContext, () -> {
            String sql = buildInsertXmlCondition(entityClass, Constants.ENTITY);
            if (Objects.requireNonNull(TableMetadata.DB_TYPE) == DbType.mysql) {
                String onDuplicateSql = buildDuplicateKeyXmlCondition(entityClass, Constants.ENTITY);
                return StrUtil.format("<script>{} {}</script>", sql, onDuplicateSql);
            }
            return sql;
        });
    }

    /**
     * 批量插入
     * @param providerContext
     * @param entities
     * @return
     * @param <T>
     * @see BaseMapper#batchInsert(List)
     */
    public <T> String batchInsert(ProviderContext providerContext, Map<String, ?> params) {
        List<T> items = (List<T>) params.get(Constants.COLLECTION);
        TableMetadata<T> tableMetadata = TableMetadata.forClass((Class<T>) entityClass(providerContext));
        for (T item : items) {
            setTableId(item, tableMetadata);
        }
        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            StringBuilder sql = new StringBuilder("<script>\n\t<foreach collection=\"" + Constants.COLLECTION + "\" item=\"" + Constants.ENTITY + "\">\n\t\t");
            String insertSql = buildInsertXmlCondition(entityClass, Constants.ENTITY);
            sql.append(insertSql);
            sql.append(";\n\t</foreach>\n</script>");
            return sql.toString();
        });
    }

    /**
     * 批量插入或更新
     * @param providerContext
     * @param params
     * @return
     * @param <T>
     * @see BaseMapper#batchInsertOrUpdate(List)
     */
    public <T> String batchInsertOrUpdate(ProviderContext providerContext, Map<String, ?> params) {
        List<T> items = (List<T>) params.get(Constants.COLLECTION);
        TableMetadata<T> tableMetadata = TableMetadata.forClass((Class<T>) entityClass(providerContext));
        for (T item : items) {
            // 生成tableId
            setTableId(item, tableMetadata);
        }
        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            StringBuilder sql = new StringBuilder("<script>\n\t<foreach collection=\"" + Constants.COLLECTION + "\" item=\"" + Constants.ENTITY + "\">\n\t\t");

            String insertSql = buildInsertXmlCondition(entityClass, Constants.ENTITY);
            sql.append(insertSql).append(" ");

            String onDuplicateSql = buildDuplicateKeyXmlCondition(entityClass, Constants.ENTITY);
            sql.append(onDuplicateSql);

            sql.append(";\n\t</foreach>\n</script>");

            return sql.toString();
        });
    }

    /**
     * 锁表批量插入或更新
     * @param providerContext
     * @param params
     * @return
     * @param <T>
     * @see BaseMapper#lockBatchInsertOrUpdate(List)
     */
    public <T> String lockBatchInsertOrUpdate(ProviderContext providerContext, Map<String, ?> params) {
        List<T> items = (List<T>) params.get(Constants.COLLECTION);
        TableMetadata<T> tableMetadata = TableMetadata.forClass((Class<T>) entityClass(providerContext));
        for (T item : items) {
            // 生成tableId
            setTableId(item, tableMetadata);
        }
        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            StringBuilder sql = new StringBuilder("<script>" +
                    "LOCK TABLES " + tableMetadata.getTableName() + " WRITE;\n" +
                    "<foreach collection=\"" + Constants.COLLECTION + "\" item=\"" + Constants.ENTITY + "\">");

            String insertSql = buildInsertXmlCondition(entityClass, Constants.ENTITY);
            sql.append(insertSql).append(" ");

            String onDuplicateSql = buildDuplicateKeyXmlCondition(entityClass, Constants.ENTITY);
            sql.append(onDuplicateSql);
            sql.append(";\n</foreach>" +
                    "</script>");
            return sql.toString();
        });
    }

    private static <T> void setTableId(T entity, TableMetadata<T> tableMetadata) {
        List<Field> idFields = tableMetadata.getIdFields();
        for (Field field : tableMetadata.getFields()) {
            if (idFields.contains(field)) {
                // 这个字段是id字段
                TableId tableId = AnnotationUtil.getAnnotation(field, TableId.class);
                IdType idType = tableId.type();
                Object idValue = ReflectUtil.getFieldValue(entity, field);
                switch (idType) {
                    case AUTO: {
                        // 自增id，@Options回填
                        if (idValue != null) {
                            ReflectUtil.setFieldValue(entity, field, null);
                        }
                        break;
                    }
                    case ASSIGN_UUID: {
                        if (idValue == null || StrUtil.isEmptyIfStr(idValue)) {
                            idValue = IdUtil.simpleUUID();
                        }
                        break;
                    }
                    case NONE: // none 默认雪花id
                    case INPUT: // 没有自定义插件，默认雪花id
                    default: // 其他情况，默认雪花id
                    case ASSIGN_ID: {
                        if (idValue == null || StrUtil.isEmptyIfStr(idValue)) {
                            idValue = IdUtil.getSnowflakeNextId();
                        }
                        break;
                    }
                }

                idValue = Convert.convert(field.getType(), idValue);
                ReflectUtil.setFieldValue(entity, field, idValue);
            }
        }
    }

    public static <T> String buildInsertXmlCondition(Class<T> entityClass, String paramName) {
        TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
        String tableName = tableMetadata.getTableName();

        // 共生成两种sql，一种是插入的列，一种是插入的value
        StringBuilder insertColumnSql = new StringBuilder("<trim suffixOverrides=\",\">");
        StringBuilder insertValueSql = new StringBuilder("<trim suffixOverrides=\",\">");

        for (Field field : tableMetadata.getFields()) {

            String property = getProperty(field, paramName);
            String column = tableMetadata.getWrappedColumn(field);

            FieldStrategy insertStrategy = getFieldStrategy(tableMetadata, field);

            // 根据插入策略生成xml条件sql
            switch (insertStrategy) {
                case DEFAULT:
                case NOT_NULL: {
                    insertColumnSql.append("<if test=\"").append(property).append(" != null\">").append(column).append(",</if>");
                    insertValueSql.append("<if test=\"").append(property).append(" != null\">").append("#{").append(property).append("}").append(",</if>");
                    break;
                }
                case NOT_EMPTY: {
                    insertColumnSql.append("<if test=\"").append(property).append(" != null and ").append(property).append(" != ''\">").append(column).append(",</if>");
                    insertValueSql.append("<if test=\"").append(property).append(" != null and ").append(property).append(" != ''\">").append("#{").append(property).append("}").append(",</if>");
                    break;
                }
                case ALWAYS: {
                    insertColumnSql.append(property).append(",");
                    insertValueSql.append("#{").append(property).append("}").append(",");
                    break;
                }
                case NEVER:
                default: {
                    break;
                }
            }
        }

        insertColumnSql.append("</trim>");
        insertValueSql.append("</trim>");

        return StrUtil.format("INSERT INTO {}({}) VALUES ({})", tableName, insertColumnSql, insertValueSql);
    }

    /**
     * 获取字段的插入策略
     * @param tableMetadata
     * @param field
     * @return
     * @param <T>
     */
    private static <T> FieldStrategy getFieldStrategy(TableMetadata<T> tableMetadata, Field field) {
        FieldStrategy insertStrategy = FieldStrategy.DEFAULT;

        if (AnnotationUtil.hasAnnotation(field, TableField.class)) {
            insertStrategy = AnnotationUtil.getAnnotation(field, TableField.class).insertStrategy();
        }

        if (insertStrategy == FieldStrategy.DEFAULT) {
            // 判断是否是字符串，如果是字符串，把DEFAULT修改为NOT_EMPTY
            if (CharSequence.class.isAssignableFrom(field.getType())) {
                insertStrategy = FieldStrategy.NOT_EMPTY;
            }
        }

        if (tableMetadata.getIdFields().contains(field)) {
            // 这个字段是id字段
            TableId tableId = AnnotationUtil.getAnnotation(field, TableId.class);
            IdType idType = tableId.type();
            if (Objects.requireNonNull(idType) == IdType.AUTO) {// 自增id，不插入
                insertStrategy = FieldStrategy.NEVER;
            }
        }
        return insertStrategy;
    }

    /**
     * 根据db类型，生成不同的onDuplicate
     * @param entityClass 实体类
     * @param paramName mybatis实体类参数名
     * @return sql
     * @param <T>
     */
    private <T> String buildDuplicateKeyXmlCondition(Class<T> entityClass, String paramName) {
        switch (Objects.requireNonNull(TableMetadata.DB_TYPE)) {
            case postgresql: {
                return buildPostgreSqlOnConflictXmlCondition(entityClass, paramName);
            }
            case mysql:
            case mariadb:
            default: {
                return buildMySqlOnDuplicateXmlCondition(entityClass, paramName);
            }
        }
    }

    private static <T> String buildMySqlOnDuplicateXmlCondition(Class<T> entityClass, String paramName) {
        TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);

        StringBuilder sql = new StringBuilder("ON DUPLICATE KEY UPDATE");
        sql.append("<trim suffixOverrides=\",\">");

        for (Field field : tableMetadata.getFields()) {
            if (tableMetadata.isIdField(field)) {
                // 跳过id字段更新
                continue;
            }
            String property = (StrUtil.isBlank(paramName) ? "" : StrUtil.addSuffixIfNot(paramName, ".")) + field.getName();
            String column = tableMetadata.getWrappedColumn(field);
            FieldStrategy insertStrategy = getFieldStrategy(tableMetadata, field);

            switch (insertStrategy) {
                case DEFAULT:
                case NOT_NULL: {
                    sql.append("<if test=\"").append(property).append(" != null\">").append(column).append(" = VALUES(").append(column).append("),</if>");
                    break;
                }
                case NOT_EMPTY: {
                    sql.append("<if test=\"").append(property).append(" != null and ").append(property).append(" != ''\">").append(column).append(" = VALUES(").append(column).append("),</if>");
                    break;
                }
                case ALWAYS: {
                    sql.append(column).append(" = VALUES(").append(column).append("),");
                    break;
                }
                case NEVER:
                default: {
                    break;
                }
            }
        }

        sql.append("</trim>");

        return sql.toString();
    }

    private static <T> String buildPostgreSqlOnConflictXmlCondition(Class<T> entityClass, String paramName) {
        TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);

        List<Field> idFields = tableMetadata.getIdFields();
        if (CollUtil.isEmpty(idFields)) {
            idFields = tableMetadata.getFields();
        }

        List<String> conflicts = new ArrayList<>();
        conflicts.add("<trim suffixOverrides=\",\">");

        for (Field field : idFields) {
            String property = (StrUtil.isBlank(paramName) ? "" : StrUtil.addSuffixIfNot(paramName, ".")) + field.getName();
            String column = tableMetadata.getWrappedColumn(field);
            FieldStrategy insertStrategy = getFieldStrategy(tableMetadata, field);

            switch (insertStrategy) {
                case DEFAULT:
                case NOT_NULL: {
                    conflicts.add("<if test=\"" + property + "  != null\">" + column + ",</if>");
                    break;
                }
                case NOT_EMPTY: {
                    conflicts.add("<if test=\"" + property + " != null and " + property + " != ''\">" + column + ",</if>");
                    break;
                }
                case ALWAYS: {
                    conflicts.add(column + ",");
                    break;
                }
                case NEVER:
                default: {
                    break;
                }
            }
        }
        conflicts.add("</trim>");

        List<String> sets = new ArrayList<>();
        sets.add("<trim suffixOverrides=\",\">");
        for (Field field : tableMetadata.getFields()) {
            String property = (StrUtil.isBlank(paramName) ? "" : StrUtil.addSuffixIfNot(paramName, ".")) + field.getName();
            String column = tableMetadata.getWrappedColumn(field);
            FieldStrategy insertStrategy = getFieldStrategy(tableMetadata, field);

            switch (insertStrategy) {
                case DEFAULT:
                case NOT_NULL: {
                    sets.add("<if test=\"" + property + "  != null\">" + column + " = EXCLUDED." + column + ",</if>");
                    break;
                }
                case NOT_EMPTY: {
                    sets.add("<if test=\"" + property + " != null and " + property + " != ''\">" + column + " = EXCLUDED." + column + ",</if>");
                    break;
                }
                case ALWAYS: {
                    sets.add(column + " = EXCLUDED." + column + ",");
                    break;
                }
                case NEVER:
                default: {
                    break;
                }
            }
        }
        sets.add("</trim>");

        return "ON CONFLICT (" + String.join("", conflicts) + ") DO UPDATE SET " + String.join("", sets);

    }
}
