package top.chukongxiang.mybatis.basemapper.sql.core;

/**
 * Wrapper基类
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-18 18:09:58
 */
public interface BaseWrapper<T, Column> {

    /**
     * 编译成SQL
     * @return SQL信息
     */
    SQLInfo<T> build();

    /**
     * where 条件
     * @return Where
     */
    String getWhereSql();

    /**
     * 获取实体类Class
     * @return 实体类
     */
    Class<T> getEntityClass();

//    /**
//     * 根据列名获取对应的列
//     * @param column
//     * @return
//     */
//    Column valueOf(String column);
}
