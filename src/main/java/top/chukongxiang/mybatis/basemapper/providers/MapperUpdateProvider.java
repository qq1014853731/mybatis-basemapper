package top.chukongxiang.mybatis.basemapper.providers;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Assert;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import top.chukongxiang.mybatis.basemapper.BaseMapper;
import top.chukongxiang.mybatis.basemapper.model.Constants;
import top.chukongxiang.mybatis.basemapper.model.annnotations.TableField;
import top.chukongxiang.mybatis.basemapper.model.enums.FieldStrategy;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperUpdate;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 18:34:38
 */
public class MapperUpdateProvider extends AbstractMapperProvider {

    /**
     * 根据id更新实体
     * @param providerContext
     * @return
     * @see BaseMapper#updateById(Object)
     */
    public String updateById(ProviderContext providerContext) {

        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

            List<Field> idFields = tableMetadata.getIdFields();
            Assert.notEmpty(idFields, "无法生成 UPDATE SQL，" + entityClass.getName() + " 没有主键");

            SQL sql = new SQL().UPDATE(tableMetadata.getTableName())
                    .SET(buildUpdateSetXmlCondition(entityClass, "item"));

            for (Field idField : idFields) {
                String idColumn = tableMetadata.getWrappedColumn(idField);
                sql.WHERE(idColumn + " = #{" + idField.getName() + "}");
            }
            return "<script>" + sql + "</script>";
        });
    }

    /**
     * 根据构造器更新数据
     * @param providerContext
     * @param params
     * @return
     * @param <T>
     * @param <E>
     * @param <Column>
     * @see BaseMapper#update(WrapperUpdate)
     */
    public <T, E extends WrapperUpdate<T, E, Column>, Column> String updateWrapper(ProviderContext providerContext, Map<String, ?> params) {
        WrapperUpdate<T, E, Column> wrapper = (WrapperUpdate<T, E, Column>) params.get(Constants.WRAPPER);
        if (wrapper == null) {
            throw new RuntimeException("wrapper 不能为空");
        }
        Class<?> entityClass = entityClass(providerContext);
        TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);
        String tableName = tableMetadata.getTableName();
        wrapper.setTableName(tableName);
        return wrapper.build().getSql();
    }


    /**
     * 构建更新时默认的SET字段SQL
     * @param entityClass 实体类
     * @return UPDATE SET XML SQL
     * @param <T> 实体类型
     */
    public static <T> String buildUpdateSetXmlCondition(Class<T> entityClass, String paramName) {
        TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

        StringBuilder setSql = new StringBuilder("<trim suffixOverrides=\",\">");

        for (Field field : tableMetadata.getFields()) {

            String property = getProperty(field, paramName);
            String column = tableMetadata.getWrappedColumn(field);

            FieldStrategy updateStrategy = getUpdateStrategy(field);

            switch (updateStrategy) {
                case DEFAULT:
                case NOT_NULL: {
                    setSql.append("<if test=\"").append(property).append(" != null\">").append(column).append(" = #{").append(property).append("},</if>");
                    break;
                }
                case NOT_EMPTY: {
                    setSql.append("<if test=\"").append(property).append(" != null and ").append(property).append(" != ''\">").append(column).append(" = #{").append(property).append("},</if>");
                    break;
                }
                case ALWAYS: {
                    setSql.append(column).append(" = #{").append(property).append("},");
                    break;
                }
                case NEVER:
                default: {
                    break;
                }
            }
        }

        setSql.append("</trim>");
        return setSql.toString();
    }

    /**
     * 获取字段更新策略
     * @param field 字段
     * @return 更新策略
     */
    private static FieldStrategy getUpdateStrategy(Field field) {
        FieldStrategy updateStrategy = FieldStrategy.DEFAULT;
        // 获取更新策略
        if (AnnotationUtil.hasAnnotation(field, TableField.class)) {
            updateStrategy = AnnotationUtil.getAnnotation(field, TableField.class).updateStrategy();
        }

        if (updateStrategy == FieldStrategy.DEFAULT) {
            // 判断是否是字符串，如果是字符串，把DEFAULT修改为NOT_EMPTY
            if (CharSequence.class.isAssignableFrom(field.getType())) {
                updateStrategy = FieldStrategy.NOT_EMPTY;
            }
        }
        return updateStrategy;
    }
}
