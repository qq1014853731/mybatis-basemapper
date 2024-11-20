package top.chukongxiang.mybatis.basemapper.sql;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.jdbc.SQL;
import top.chukongxiang.mybatis.basemapper.providers.TableMetadata;
import top.chukongxiang.mybatis.basemapper.sql.core.AbstractWrapper;
import top.chukongxiang.mybatis.basemapper.sql.core.SQLInfo;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperLambda;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperUpdate;
import top.chukongxiang.mybatis.basemapper.utils.SqlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 列类型是lambda表达式的更新Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-14 18:45:17
 */
public class LambdaUpdateWrapper<T>
        extends AbstractWrapper<T, LambdaUpdateWrapper<T>, Func1<T, ?>>
        implements WrapperLambda<T, LambdaUpdateWrapper<T>>, WrapperUpdate<T, LambdaUpdateWrapper<T>, Func1<T, ?>> {

    private String tableName;

    private final List<String> setSqls = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();

    public LambdaUpdateWrapper() {
        super(null, null);
    }

    public LambdaUpdateWrapper(T entity) {
        super(entity, (Class<T>) entity.getClass());
    }

    public LambdaUpdateWrapper(Class<T> entityClass) {
        super(entityClass);
        if (entityClass != null) {
            TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
            if (tableMetadata != null) {
                this.tableName = tableMetadata.getTableName();
            }
        }
    }

    public <V> LambdaUpdateWrapper<T> set(Func1<T, V> column, V value) {
        return set(true, column, value);
    }

    @Override
    public LambdaUpdateWrapper<T> set(boolean condition, Func1<T, ?> column, Object value) {
        if (condition) {
            setSqls.add(getColumn(column) + " = " + getWrap(setSqls.size()));
            values.add(value);
        }
        return this;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public UpdateWrapper<T> update() {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        BeanUtil.copyProperties(this, updateWrapper);
        return updateWrapper;
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

        // where 重新设置values
        String whereSql = getWhereSql();
        List<Object> whereValues = new ArrayList<>(super.getValues());
        if (StrUtil.isNotBlank(whereSql)) {
            // 需重新处理
            whereSql = " WHERE " + resetSql(whereSql, whereValues.size());
            values.addAll(whereValues);
        }

        SQL sql = builder.WHERE(whereSql);
        return SQLInfo.<T>builder().entityClass(entityClass).sql(SqlUtil.normalSql(sql)).values(values).build();
    }
}
