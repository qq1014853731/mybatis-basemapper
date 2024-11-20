package top.chukongxiang.mybatis.basemapper.sql;

/**
 * 条件构造器
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-13 09:31:18
 */
public class Wrappers {

    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    public static <T> QueryWrapper<T> query(T entity) {
        return new QueryWrapper<>(entity);
    }

    public static <T> QueryWrapper<T> query(Class<T> entityClass) {
        return new QueryWrapper<>(entityClass);
    }

    public static <T> LambdaQueryWrapper<T> lambdaQuery() {
        return new LambdaQueryWrapper<>();
    }

    public static <T> LambdaQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaQueryWrapper<>(entity);
    }

    public static <T> LambdaQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
        return new LambdaQueryWrapper<>(entityClass);
    }

    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    public static <T> UpdateWrapper<T> update(T entity) {
        return new UpdateWrapper<>(entity);
    }

    public static <T> UpdateWrapper<T> update(Class<T> entityClass) {
        return new UpdateWrapper<>(entityClass);
    }

    public static <T> LambdaUpdateWrapper<T> lambdaUpdate() {
        return new LambdaUpdateWrapper<>();
    }

    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        return new LambdaUpdateWrapper<>(entity);
    }

    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(Class<T> entityClass) {
        return new LambdaUpdateWrapper<>(entityClass);
    }

    public static <T> EmptyWrapper<T> emptyWrapper() {
        return emptyWrapper(null);
    }

    public static <T> EmptyWrapper<T> emptyWrapper(Class<T> entityClass) {
        return new EmptyWrapper<>(entityClass);
    }

}
