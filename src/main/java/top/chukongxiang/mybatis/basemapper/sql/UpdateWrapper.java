package top.chukongxiang.mybatis.basemapper.sql;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.Setter;
import org.apache.ibatis.jdbc.SQL;
import top.chukongxiang.mybatis.basemapper.providers.TableMetadata;
import top.chukongxiang.mybatis.basemapper.sql.core.AbstractWrapper;
import top.chukongxiang.mybatis.basemapper.sql.core.SQLInfo;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperString;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperUpdate;
import top.chukongxiang.mybatis.basemapper.utils.SqlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 列类型是字符串的更新Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-14 18:45:17
 */
public class UpdateWrapper<T>
        extends AbstractWrapper<T, UpdateWrapper<T>, String>
        implements WrapperUpdate<T, UpdateWrapper<T>, String>, WrapperString<T, UpdateWrapper<T>> {

    @Setter
    private String tableName;

    private final List<String> setSqls = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();

    public UpdateWrapper() {
        super(null, null);
    }

    public UpdateWrapper(T entity) {
        super(entity, (Class<T>) entity.getClass());
    }

    public UpdateWrapper(Class<T> entityClass) {
        super(entityClass);
        if (entityClass != null) {
            TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
            if (tableMetadata != null) {
                this.tableName = tableMetadata.getTableName();
            }
        }
    }
    @Override
    public UpdateWrapper<T> set(boolean condition, String column, Object value) {
        if (condition) {
            setSqls.add("SET " + getColumn(column) + " = " + getWrap(setSqls.size()));
            values.add(value);
        }
        return this;
    }

    public LambdaUpdateWrapper<T> lambda() {
        LambdaUpdateWrapper<T> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        BeanUtil.copyProperties(this, lambdaUpdateWrapper);
        return lambdaUpdateWrapper;
    }

    @Override
    public SQLInfo<T> build() {

        if (StrUtil.isBlank(this.tableName)) {
            if (this.entityClass != null) {
                this.tableName = TableMetadata.forClass(this.entityClass).getTableName();
            }
        }

        Assert.notBlank(this.tableName);

        // set
        String setSql = StrUtil.join(", ", setSqls);
        Assert.notBlank(setSql);

        SQL builder = new SQL().UPDATE(this.tableName).SET(setSql);

        // where 重置
        String whereSql = getWhereSql();
        List<Object> whereValues = new ArrayList<>(super.getValues());
        if (StrUtil.isNotBlank(whereSql)) {
            whereSql = " WHERE " + resetSql(whereSql, whereValues.size());
            values.addAll(whereValues);
        }

        String sql = builder.WHERE(whereSql).toString();
        return SQLInfo.<T>builder().entityClass(entityClass).sql(SqlUtil.normalSql(sql)).values(values).build();
    }
}
