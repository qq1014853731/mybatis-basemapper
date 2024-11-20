package top.chukongxiang.mybatis.basemapper.sql;

import org.apache.ibatis.jdbc.SQL;
import top.chukongxiang.mybatis.basemapper.providers.TableMetadata;
import top.chukongxiang.mybatis.basemapper.sql.core.SQLInfo;
import top.chukongxiang.mybatis.basemapper.sql.core.Wrapper;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 空查询wrapper，仅仅支持没有任何where条件
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-15 09:12:15
 */
public class EmptyWrapper<T> implements WrapperQuery<T, EmptyWrapper<T>, String> {

    private final Class<T> entityClass;
    private String tableName;

    public EmptyWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
        if (this.entityClass != null) {
            this.tableName = TableMetadata.forClass(entityClass).getTableName();
        }
    }

    @Override
    public SQLInfo<T> build() {
        return SQLInfo.<T>builder()
                .entityClass(entityClass)
                .sql(new SQL().SELECT(getSqlSelect()).FROM(getTableName()).toString())
                .build();
    }

    @Override
    public String getWhereSql() {
        return "";
    }

    @Override
    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public EmptyWrapper<T> eq(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> ne(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> gt(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> lt(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> ge(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> le(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> in(boolean condition, String s, Object... values) {
        return this;
    }

    @Override
    public EmptyWrapper<T> in(boolean condition, String s, Collection<?> values) {
        return this;
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> EmptyWrapper<T> in(boolean condition, String s, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return this;
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> EmptyWrapper<T> exists(boolean condition, String s, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return this;
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> EmptyWrapper<T> notExists(boolean condition, String s, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return this;
    }

    @Override
    public EmptyWrapper<T> notIn(boolean condition, String s, Object... values) {
        return this;
    }

    @Override
    public EmptyWrapper<T> notIn(boolean condition, String s, Collection<?> values) {
        return this;
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> EmptyWrapper<T> notIn(boolean condition, String s, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return this;
    }

    @Override
    public EmptyWrapper<T> like(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> notLike(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> likeLeft(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> notLikeLeft(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> likeRight(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> notLikeRight(boolean condition, String s, Object value) {
        return this;
    }

    @Override
    public EmptyWrapper<T> isNull(boolean condition, String... strings) {
        return this;
    }

    @Override
    public EmptyWrapper<T> isNotNull(boolean condition, String... strings) {
        return this;
    }

    @Override
    public <V> EmptyWrapper<T> between(boolean condition, String s, V start, V end) {
        return this;
    }

    @Override
    public EmptyWrapper<T> and(boolean condition) {
        return this;
    }

    @Override
    public EmptyWrapper<T> and(boolean condition, Consumer<Wrapper<T, ? super EmptyWrapper<T>, String>> consumer) {
        return this;
    }

    @Override
    public EmptyWrapper<T> or(boolean condition) {
        return this;
    }

    @Override
    public EmptyWrapper<T> or(boolean condition, Consumer<Wrapper<T, ? super EmptyWrapper<T>, String>> consumer) {
        return this;
    }

    @Override
    public EmptyWrapper<T> select(boolean condition, boolean distinct, String... strings) {
        return this;
    }

    @Override
    public EmptyWrapper<T> groupBy(boolean condition, String... strings) {
        return this;
    }

    @Override
    public <F extends Wrapper<T, F, String>> EmptyWrapper<T> having(boolean condition, Consumer<Wrapper<T, F, String>> consumer) {
        return this;
    }

    @Override
    public EmptyWrapper<T> orderByAsc(boolean condition, String... strings) {
        return this;
    }

    @Override
    public EmptyWrapper<T> orderByDesc(boolean condition, String... strings) {
        return this;
    }

    @Override
    public EmptyWrapper<T> orderBy(boolean condition, boolean isAsc, String... strings) {
        return this;
    }

    @Override
    public EmptyWrapper<T> last(boolean condition, String sql) {
        return this;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public List<OrderBy> getOrderBys() {
        return Collections.emptyList();
    }
}
