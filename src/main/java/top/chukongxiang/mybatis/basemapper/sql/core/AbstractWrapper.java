package top.chukongxiang.mybatis.basemapper.sql.core;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import top.chukongxiang.mybatis.basemapper.model.Constants;
import top.chukongxiang.mybatis.basemapper.model.enums.FieldStrategy;
import top.chukongxiang.mybatis.basemapper.model.enums.SqlCondition;
import top.chukongxiang.mybatis.basemapper.model.enums.WrapperSqlCondition;
import top.chukongxiang.mybatis.basemapper.providers.AbstractMapperProvider;
import top.chukongxiang.mybatis.basemapper.providers.TableMetadata;
import top.chukongxiang.mybatis.basemapper.sql.ChildrenQueryWrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 抽象基础条件Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-13 09:31:30
 */
public abstract class AbstractWrapper<T, E extends Wrapper<T, E, Column>, Column> implements Wrapper<T, E, Column> {

    protected Class<T> entityClass;

    private String sql = "";

    private String condition = " AND ";

    @Getter
    private final List<Object> values = new ArrayList<>();

    protected int paramIndex = 0;

    public AbstractWrapper(T entity) {
        this(entity, (Class<T>) entity.getClass());
    }

    public AbstractWrapper(Class<T> entityClass) {
        this(null, entityClass);
    }

    public AbstractWrapper(T entity, Class<T> entityClass) {
        this.entityClass = entityClass;
        if (entity != null) {
            build(entity);
        }
    }

    @Override
    public E eq(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.EQ, value).and();
    }

    @Override
    public E ne(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.NE, value).and();
    }

    @Override
    public E gt(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.GT, value).and();
    }

    @Override
    public E lt(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.LT, value).and();
    }

    @Override
    public E ge(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.GE, value).and();
    }

    @Override
    public E le(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.LE, value).and();
    }

    @Override
    public E in(boolean condition, Column column, Object... values) {
        return build(condition, column, WrapperSqlCondition.IN, ListUtil.toList(values)).and();
    }

    @Override
    public E in(boolean condition, Column column, Collection<?> values) {
        return build(condition, column, WrapperSqlCondition.IN, values).and();
    }

    @Override
    public E notIn(boolean condition, Column column, Object... values) {
        return build(condition, column, WrapperSqlCondition.NOT_IN, ListUtil.toList(values)).and();
    }

    @Override
    public E notIn(boolean condition, Column column, Collection<?> values) {
        return build(condition, column, WrapperSqlCondition.NOT_IN, values).and();
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> E in(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        SQLInfo<M> sqlInfo = null;
        if (condition) {
            ChildrenQueryWrapper childrenQueryWrapper = new ChildrenQueryWrapper();
            WrapperQuery<M, F, C> wrapperQuery = wrapper.apply(childrenQueryWrapper);
            sqlInfo = wrapperQuery.build();
        }
        return build(condition, column, WrapperSqlCondition.IN_QUERY, sqlInfo).and();
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> E exists(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        SQLInfo<M> sqlInfo = null;
        if (condition) {
            ChildrenQueryWrapper childrenQueryWrapper = new ChildrenQueryWrapper();
            WrapperQuery<M, F, C> wrapperQuery = wrapper.apply(childrenQueryWrapper);
            sqlInfo = wrapperQuery.build();
        }
        return build(condition, column, WrapperSqlCondition.EXISTS, sqlInfo);
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> E notExists(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        SQLInfo<M> sqlInfo = null;
        if (condition) {
            ChildrenQueryWrapper childrenQueryWrapper = new ChildrenQueryWrapper();
            WrapperQuery<M, F, C> wrapperQuery = wrapper.apply(childrenQueryWrapper);
            sqlInfo = wrapperQuery.build();
        }
        return build(condition, column, WrapperSqlCondition.NOT_EXISTS, sqlInfo);
    }

    @Override
    public <M, F extends Wrapper<M, F, C>, C> E notIn(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        SQLInfo<M> sqlInfo = null;
        if (condition) {
            ChildrenQueryWrapper childrenQueryWrapper = new ChildrenQueryWrapper();
            WrapperQuery<M, F, C> wrapperQuery = wrapper.apply(childrenQueryWrapper);
            sqlInfo = wrapperQuery.build();
        }
        return build(true, column, WrapperSqlCondition.NOT_IN_QUERY, sqlInfo).and();
    }

    @Override
    public E like(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.LIKE, value).and();
    }

    @Override
    public E notLike(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.NOT_LIKE, value).and();
    }

    @Override
    public E likeLeft(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.LIKE_LEFT, value).and();
    }

    @Override
    public E notLikeLeft(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.NOT_LIKE_LEFT, value).and();
    }

    @Override
    public E likeRight(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.LIKE_RIGHT, value).and();
    }

    @Override
    public E notLikeRight(boolean condition, Column column, Object value) {
        return build(condition, column, WrapperSqlCondition.NOT_LIKE_RIGHT, value).and();
    }

    @SafeVarargs
    @Override
    public final E isNull(boolean condition, Column... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            for (Column column : columns) {
                build(true, column, WrapperSqlCondition.IS_NULL, null);
            }
        }
        return and();
    }

    @SafeVarargs
    @Override
    public final E isNotNull(boolean condition, Column... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            for (Column column : columns) {
                build(true, column, WrapperSqlCondition.IS_NOT_NULL, columns);
            }
        }
        return and();
    }

    @Override
    public final <V> E between(boolean condition, Column column, V start, V end) {
        return build(condition, column, WrapperSqlCondition.BETWEEN, new Object[] { start, end }).and();
    }

    @Override
    public E and(boolean condition) {
        if (condition) {
            this.condition = " AND ";
        }
        return (E) this;
    }

    @Override
    public E and(boolean condition, Consumer<Wrapper<T, ? super E, Column>> consumer) {
        if (condition && consumer != null) {
            AbstractWrapper<T, E, Column> wrapper = new AbstractWrapper<T, E, Column>(this.entityClass) {};
            wrapper.paramIndex = paramIndex;
            consumer.accept(wrapper);
            String childrenSql = wrapper.getWhereSql();
            if (StrUtil.isNotBlank(childrenSql)) {
                this.values.addAll(wrapper.values);
                this.sql += " AND (" + childrenSql + ")";
            }
        }
        return and();
    }

    /**
     * 接下来的一个条件是OR链接
     * @return
     */
    @Override
    public E or(boolean condition) {
        if (condition) {
            this.condition = " OR ";
        }
        return (E) this;
    }

    @Override
    public E or(boolean condition, Consumer<Wrapper<T, ? super E, Column>> consumer) {
        if (condition && consumer != null) {
            AbstractWrapper<T, E, Column> wrapper = new AbstractWrapper<T, E, Column>(this.entityClass) {};
            wrapper.paramIndex = paramIndex;
            consumer.accept(wrapper);
            String childrenSql = wrapper.getWhereSql();
            if (StrUtil.isNotBlank(childrenSql)) {
                this.values.addAll(wrapper.values);
                this.sql += " OR (" + childrenSql + ")";
            }
        }
        return and();
    }

    private E build(boolean condition, Column column, WrapperSqlCondition sqlCondition, Object value) {
        if (condition) {
            String wrappedColumn = getColumn(column);
            Assert.notBlank(wrappedColumn);
            switch (sqlCondition) {
                case IN:
                case NOT_IN: {
                    List<String> ins = new ArrayList<>();
                    for (Object item : (Iterable<?>) value) {
                        ins.add(getWrap(this.paramIndex++));
                        values.add(item);
                    }
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue() + "(" + String.join(", ", ins) + ")");
                    break;
                }
                case EXISTS:
                case NOT_EXISTS:
                case IN_QUERY:
                case NOT_IN_QUERY: {
                    SQLInfo<?> sqlInfo = (SQLInfo<?>) value;
                    // 重设
                    String sql = sqlInfo.getSql();
                    List<Object> values = sqlInfo.getValues();
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue() + " ( " + resetSql(sql, values.size()) + " )");
                    this.values.addAll(values);
                    break;
                }
                case NOT_LIKE:
                case LIKE: {
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue() + " CONCAT('%', " + getWrap(this.paramIndex++) + ", '%')");
                    values.add(value);
                    break;
                }
                case NOT_LIKE_LEFT:
                case LIKE_LEFT: {
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue() + " CONCAT('%', " + getWrap(this.paramIndex++) + ")");
                    values.add(value);
                    break;
                }
                case NOT_LIKE_RIGHT:
                case LIKE_RIGHT: {
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue() + " CONCAT(" + getWrap(this.paramIndex++) + ", '%')");
                    values.add(value);
                    break;
                }
                case IS_NULL:
                case IS_NOT_NULL: {
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue());
                    break;
                }
                case BETWEEN:
                case NOT_BETWEEN: {
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue() + getWrap(this.paramIndex++) + " AND " + getWrap(this.paramIndex++));
                    Object[] betweenValue = (Object[]) value;
                    values.add(betweenValue[0]);
                    values.add(betweenValue[1]);
                    break;
                }
                default: {
                    this.sql += (this.condition + wrappedColumn + " " + sqlCondition.getValue() + " " + getWrap(this.paramIndex++));
                    values.add(value);
                    break;
                }
            }
        }
        return (E) this;
    }

    /**
     * 根据实体生成查询条件
     * @param entity 实体类
     */
    private void build(T entity) {
        TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
        // 遍历字段和属性
        for (Field field : tableMetadata.getFields()) {
            String column = tableMetadata.getWrappedColumn(field);
            // 获取条件
            SqlCondition sqlCondition = AbstractMapperProvider.getSqlCondition(field);

            // 转换条件
            WrapperSqlCondition condition = WrapperSqlCondition.valueOf(sqlCondition.name());

            // where策略
            FieldStrategy whereStrategy = AbstractMapperProvider.getWhereStrategy(field);

            Object value = ReflectUtil.getFieldValue(entity, field);

            // 根据策略构造条件，因为仅仅调用父类的build方法，父类并没有定义具体类型，所以强转没有问题
            switch (whereStrategy) {
                case DEFAULT:
                case NOT_NULL: {
                    if (Objects.nonNull(value)) {
                        build(true, (Column) column, condition, value);
                    }
                    break;
                }
                case NOT_EMPTY: {
                    if (StrUtil.isNotEmpty((String) value)) {
                        build(true, (Column) column, condition, value);
                    }
                    break;
                }
                case ALWAYS: {
                    build(true, (Column) column, condition, value);
                    break;
                }
                case NEVER:
                default: {
                    break;
                }
            }
        }
    }

    @SneakyThrows
    protected final String getColumn(Column column) {
        String wrappedColumn = null;
        if (column instanceof String) {
            // 判断column是否是一个函数，如果是一个函数则不wrap
            if (ReUtil.isMatch("^[a-zA-Z_]+\\s*\\((.*)\\)$", (String) column)) {
                return (String) column;
            }
            if (this.entityClass == null) {
                wrappedColumn = TableMetadata.autoWrap(String.valueOf(column));
            } else {
                wrappedColumn = TableMetadata.forClass(this.entityClass).lookupColumn(String.valueOf(column));
            }
            return wrappedColumn == null ? String.valueOf(column) : wrappedColumn;
        } else if (column instanceof Func1) {
            if (this.entityClass == null) {
                this.entityClass = (Class<T>) LambdaUtil.getRealClass((Func1<?, ?>) column);
                return getColumn(column);
            } else {
                wrappedColumn = TableMetadata.forClass(this.entityClass).lookupColumn(LambdaUtil.getFieldName((Func1<?, ?>) column));
            }
        }
        return wrappedColumn;
    }

    @Override
    public SQLInfo<T> build() {
        return SQLInfo.<T>builder().entityClass(entityClass).sql(this.getWhereSql()).values(this.values).build();
    }

    @Override
    public String getWhereSql() {
        String whereSql = StrUtil.trim(this.sql);
        if (StrUtil.isNotBlank(whereSql)) {
            whereSql = StrUtil.removePrefixIgnoreCase(whereSql, "AND ");
            whereSql = StrUtil.removePrefixIgnoreCase(whereSql, "OR ");
            return StrUtil.trim(whereSql);
        }
        return "";
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }


    protected final String getWrap(int index) {
        return "#{" + Constants.WRAPPER + ".values[" + index + "]}";
    }

    protected final String resetSql(String sql, int paramsCount) {
        String newSql = sql;
        int startIndex = 0;
        for (int i = 0; i < paramsCount; i++) {
            String src = getWrap(i);
            String dest = getWrap(paramIndex++);
            String tmpStr = newSql.substring(startIndex);
            int index = tmpStr.indexOf(src);
            tmpStr = tmpStr.replace(src, dest);
            newSql = newSql.substring(0, startIndex) + tmpStr;
            startIndex = startIndex + index + (dest.length() - src.length());
        }
        return newSql;
    }

}
