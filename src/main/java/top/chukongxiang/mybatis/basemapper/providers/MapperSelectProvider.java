package top.chukongxiang.mybatis.basemapper.providers;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import top.chukongxiang.mybatis.basemapper.BaseMapper;
import top.chukongxiang.mybatis.basemapper.model.Constants;
import top.chukongxiang.mybatis.basemapper.sql.Wrappers;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperQuery;
import top.chukongxiang.mybatis.basemapper.utils.SqlUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 需要保证 mybatis-spring-boot 版本 > 2.0.2
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 14:13:21
 */
public class MapperSelectProvider extends AbstractMapperProvider {

    /**
     * 查询所有数据
     * @see BaseMapper#selectAll()
     * @param providerContext
     * @return
     */
    public String selectAll(ProviderContext providerContext) {
        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

            String tableName = tableMetadata.getTableName();

            return new SQL()
                    .SELECT(tableMetadata.getSelectColumn())
                    .FROM(tableName)
                    .toString();
        });
    }

    /**
     * 根据主键查询实体
     * @see BaseMapper#selectById(java.io.Serializable)
     * @param providerContext
     * @return
     */
    public String selectById(ProviderContext providerContext) {

        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);
            TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

            String tableName = tableMetadata.getTableName();

            SQL sql = new SQL()
                    .SELECT(tableMetadata.getSelectColumn())
                    .FROM(tableName);

            for (Field idField : tableMetadata.getIdFields()) {
                String idColumn = tableMetadata.getWrappedColumn(idField);
                sql.WHERE(idColumn + " = #{param1}");
            }

            return sql.toString();
        });

    }

    /**
     * 根据id数组查询实体列表
     *
     * @param providerContext
     * @see BaseMapper#selectByIds(Collection)
     * @return
     */
    public String selectByIds(ProviderContext providerContext) {
        return getCachedSql(providerContext, () -> {
            Class<?> entityClass = entityClass(providerContext);

            TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

            String tableName = tableMetadata.getTableName();

            SQL sql = new SQL()
                    .SELECT(tableMetadata.getSelectColumn())
                    .FROM(tableName)
                    ;

            for (Field idField : tableMetadata.getIdFields()) {
                String idColumn = tableMetadata.getWrappedColumn(idField);
                sql.WHERE(idColumn + " IN <foreach collection=\"" + Constants.COLLECTION + "\" item=\"id\" open=\"(\" separator=\",\" close=\")\">#{id}</foreach>");
            }

            return "<script>" + sql + "</script>";
        });
    }

    /**
     *
     * @param providerContext
     * @param params
     * @return
     * @param <T>
     * @see BaseMapper#selectOne(WrapperQuery)
     */
    public <T, E extends WrapperQuery<T, E, Column>, Column> String selectWrapper(ProviderContext providerContext, Map<String, Object> params) {
        WrapperQuery<T, E, Column> wrapper = MapUtil.get(params, Constants.WRAPPER, new TypeReference<WrapperQuery<T, E, Column>>() {});
        Class<?> entityClass = entityClass(providerContext);
        String sql;
        if (wrapper != null) {
            TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);
            String tableName = tableMetadata.getTableName();
            wrapper.setTableName(tableName);
            sql = wrapper.build().getSql();
        } else {
            sql = Wrappers.emptyWrapper(entityClass).build().getSql();
        }
        return sql;
    }

    /**
     * @param providerContext
     * @param params
     * @return
     * @param <T>
     * @param <E>
     * @param <Column>
     * @see BaseMapper#count()
     * @see BaseMapper#count(WrapperQuery)
     */
    public <T, E extends WrapperQuery<T, E, Column>, Column> String countWrapper(ProviderContext providerContext, Map<String, Object> params) {
        Class<?> entityClass = entityClass(providerContext);
        String tableName = TableMetadata.forClass(entityClass).getTableName();
        WrapperQuery<T, E, Column> wrapper = MapUtil.get(params, Constants.WRAPPER, new TypeReference<WrapperQuery<T, E, Column>>() {});
        if (wrapper != null) {
            wrapper.setTableName(tableName);
            String sql = SqlUtil.normalSql(wrapper.build().getSql());
            return StrUtil.format("SELECT COUNT(*) FROM ({}) cnt", sql);
        }
        return SqlUtil.normalSql(new SQL().SELECT("COUNT(*)").FROM(tableName));
    }


}
