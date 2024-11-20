package top.chukongxiang.mybatis.basemapper.sql.core;

import top.chukongxiang.mybatis.basemapper.sql.ChildrenQueryWrapper;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 基础 where Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-15 15:57:17
 */
public interface Wrapper<T, E extends Wrapper<T, E, Column>, Column> extends BaseWrapper<T, Column> {

    /**
     * column = value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E eq(boolean condition, Column column, Object value);
    default E eq(Column column, Object value) {
        return eq(true, column, value);
    }

    /**
     * column != value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E ne(boolean condition, Column column, Object value);
    default E ne(Column column, Object value) {
        return ne(true, column, value);
    }

    /**
     * column > value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E gt(boolean condition, Column column, Object value);
    default E gt(Column column, Object value) {
        return gt(true, column, value);
    }

    /**
     * column < value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E lt(boolean condition, Column column, Object value);
    default E lt(Column column, Object value) {
        return lt(true, column, value);
    }

    /**
     * column >= value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E ge(boolean condition, Column column, Object value);
    default E ge(Column column, Object value) {
        return gt(true, column, value);
    }

    /**
     * column <= value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E le(boolean condition, Column column, Object value);
    default E le(Column column, Object value) {
        return le(true, column, value);
    }

    /**
     * column in (...values)
     * @param condition 是否执行
     * @param column 列
     * @param values 值
     * @return Wrapper
     */
    E in(boolean condition, Column column, Object... values);
    default E in(Column column, Object... values) {
        return in(true, column, values);
    }

    /**
     * column in (...values)
     * @param condition 是否执行
     * @param column 列
     * @param values 值
     * @return Wrapper
     */
    E in(boolean condition, Column column, Collection<?> values);
    default E in(Column column, Collection<?> values) {
        return in(true, column, values);
    }

    /**
     * in (SELECT x FROM x ....)
     * @param condition
     * @param column
     * @param wrapper
     * @return
     */
    <M, F extends Wrapper<M, F, C>, C> E in(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper);
    default <M, F extends Wrapper<M, F, C>, C> E in(Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return in(true, column, wrapper);
    }

    /**
     * exists(select xx from xxx)
     * @param condition
     * @param column
     * @param wrapper
     * @return
     * @param <M>
     * @param <F>
     * @param <C>
     */
    <M, F extends Wrapper<M, F, C>, C> E exists(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper);
    default <M, F extends Wrapper<M, F, C>, C> E exists(Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return exists(true, column, wrapper);
    }

    /**
     * not exists(select xx from xxx)
     * @param condition
     * @param column
     * @param wrapper
     * @return
     * @param <M>
     * @param <F>
     * @param <C>
     */
    <M, F extends Wrapper<M, F, C>, C> E notExists(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper);
    default <M, F extends Wrapper<M, F, C>, C> E notExists(Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return notExists(true, column, wrapper);
    }

    /**
     * column not in (...values)
     * @param condition 是否执行
     * @param column 列
     * @param values 值
     * @return Wrapper
     */
    E notIn(boolean condition, Column column, Object... values);
    default E notIn(Column column, Object... values) {
        return notIn(true, column, values);
    }

    /**
     * column not in (...values)
     * @param condition 是否执行
     * @param column 列
     * @param values 值
     * @return Wrapper
     */
    E notIn(boolean condition, Column column, Collection<?> values);
    default E notIn(Column column, Collection<?> values) {
        return notIn(true, column, values);
    }

    /**
     * not in (SELECT x FROM x ....)
     * @param condition
     * @param column
     * @param consumer
     * @return
     * @param <M>
     * @param <F>
     * @param <C>
     */
    <M, F extends Wrapper<M, F, C>, C> E notIn(boolean condition, Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper);
    default <M, F extends Wrapper<M, F, C>, C> E notIn(Column column, Function<ChildrenQueryWrapper, WrapperQuery<M, F, C>> wrapper) {
        return notIn(true, column, wrapper);
    }


    /**
     * column like %value%
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E like(boolean condition, Column column, Object value);
    default E like(Column column, Object value) {
        return like(true, column, value);
    }

    /**
     * column not like %value%
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E notLike(boolean condition, Column column, Object value);
    default E notLike(Column column, Object value) {
        return notLike(true, column, value);
    }

    /**
     * column like %value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E likeLeft(boolean condition, Column column, Object value);
    default E likeLeft(Column column, Object value) {
        return likeLeft(true, column, value);
    }

    /**
     * column not like %value
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E notLikeLeft(boolean condition, Column column, Object value);
    default E notLikeLeft(Column column, Object value) {
        return notLikeLeft(true, column, value);
    }

    /**
     * column like value%
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E likeRight(boolean condition, Column column, Object value);
    default E likeRight(Column column, Object value) {
        return likeRight(true, column, value);
    }

    /**
     * column not like value%
     * @param condition 是否执行
     * @param column 列
     * @param value 值
     * @return Wrapper
     */
    E notLikeRight(boolean condition, Column column, Object value);
    default E notLikeRight(Column column, Object value) {
        return notLikeRight(true, column, value);
    }

    /**
     * column is null
     * @param condition 是否执行
     * @param columns 列
     * @return Wrapper
     */
    E isNull(boolean condition, Column... columns);
    default E isNull(Column... columns) {
        return isNull(true, columns);
    }

    /**
     * column is not null
     * @param condition 是否执行
     * @param columns 列
     * @return Wrapper
     */
    E isNotNull(boolean condition, Column... columns);
    default E isNotNull(Column... columns) {
        return isNotNull(true, columns);
    }

    /**
     * column between start and end
     * @param condition 是否执行
     * @param column 列
     * @param start 开始
     * @param end 结束
     * @return Wrapper
     * @param <V> 值的类型
     */
    <V> E between(boolean condition, Column column, V start, V end);
    default <V> E between(Column column, V start, V end) {
        return between(true, column, start, end);
    }

    /**
     * 接下来的条件是否是and
     * @return Wrapper
     */
    E and(boolean condition);
    default E and() {
        return and(true);
    }

    /**
     * and ($wrapper)
     * @param condition 是否执行
     * @param consumer and的wrapper
     * @return Wrapper
     */
    E and(boolean condition, Consumer<Wrapper<T, ? super E, Column>> consumer);
    default E and(Consumer<Wrapper<T, ? super E, Column>> consumer) {
        return and(true, consumer);
    }

    /**
     * 接下来的条件是否是 or
     * @return Wrapper
     */
    E or(boolean condition);
    default E or() {
        return or(true);
    }

    /**
     * 接下来的条件是否是or
     * @param condition 是否执行
     * @param consumer or的wrapper
     * @return Wrapper
     */
    E or(boolean condition, Consumer<Wrapper<T, ? super E, Column>> consumer);
    default E or(Consumer<Wrapper<T, ? super E, Column>> consumer) {
        return or(true, consumer);
    }


}
