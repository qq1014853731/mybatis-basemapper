package top.chukongxiang.mybatis.basemapper.sql;

/**
 * 用于子查询，构造器传入子查询Wrapper，接受QueryWrapper,子查询必须指定实体类
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-19 08:46:23
 */
public final class ChildrenQueryWrapper {

    public <T> LambdaQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaQueryWrapper<>(entity);
    }

    public <T> LambdaQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
        return new LambdaQueryWrapper<>(entityClass);
    }

    public <T> QueryWrapper<T> query(Class<T> entityClass) {
        return new QueryWrapper<>(entityClass);
    }

    public <T> QueryWrapper<T> query(T entity) {
        return new QueryWrapper<>(entity);
    }

}
