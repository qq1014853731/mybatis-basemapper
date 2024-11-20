package top.chukongxiang.mybatis.basemapper.providers;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import top.chukongxiang.mybatis.basemapper.BaseMapper;
import top.chukongxiang.mybatis.basemapper.model.Constants;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperQuery;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-02 14:27:38
 */
public class MapperDeleteProvider extends AbstractMapperProvider {

    /**
     * 根据主键删除数据
     * @param providerContext
     * @return
     * @see BaseMapper#deleteById(Serializable)
     */
    public String deleteById(ProviderContext providerContext) {
        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

            List<Field> idFields = tableMetadata.getIdFields();
            Assert.notEmpty(idFields, "无法生成 DELETE SQL，" + entityClass.getName() + " 没有主键");

            SQL sql = new SQL().DELETE_FROM(tableMetadata.getTableName());

            for (Field idField : idFields) {
                String column = tableMetadata.getWrappedColumn(idField);
                sql.WHERE(column + " = #{param1}");
            }

            return sql.toString();
        });
    }

    /**
     * 根据主键删除数据
     * @param providerContext
     * @return
     * @see BaseMapper#deleteByIds(java.util.Collection)
     */
    public String deleteByIds(ProviderContext providerContext) {
        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

            List<Field> idFields = tableMetadata.getIdFields();
            Assert.notEmpty(idFields, "无法生成 DELETE SQL，" + entityClass.getName() + " 没有主键");

            SQL sql = new SQL().DELETE_FROM(tableMetadata.getTableName());

            for (Field idField : tableMetadata.getIdFields()) {
                String idColumn = tableMetadata.getWrappedColumn(idField);
                sql.WHERE(idColumn + " IN <foreach collection=\"" + Constants.COLLECTION + "\" item=\"id\" open=\"(\" separator=\",\" close=\")\">#{id}</foreach>");
            }

            return "<script>" + sql + "</script>";
        });
    }

    /**
     * 根据主键删除数据
     * @param providerContext
     * @return
     * @see BaseMapper#deleteByIdsArr(Serializable...)
     */
    public String deleteByIdsArr(ProviderContext providerContext) {
        return deleteByIds(providerContext);
    }

    /**
     * 根据构造器条件删除数据
     * @param providerContext
     * @param params
     * @return
     * @param <T>
     * @param <E>
     * @param <Column>
     * @see BaseMapper#delete(WrapperQuery)
     */
    public <T, E extends WrapperQuery<T, E, Column>, Column> String deleteWrapper(ProviderContext providerContext, Map<String, ?> params) {
        Class<?> entityClass = entityClass(providerContext);
        TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);
        String tableName = tableMetadata.getTableName();
        String sql = "DELETE FROM " + tableName;
        WrapperQuery<T, E, Column> queryWrapper = (WrapperQuery<T, E, Column>) params.get(Constants.WRAPPER);
        if (queryWrapper != null) {
            String whereSql = queryWrapper.getWhereSql();
            if (StrUtil.isNotBlank(whereSql)) {
                sql = sql + " WHERE " + whereSql;
            }
        }
        return sql;
    }

}
