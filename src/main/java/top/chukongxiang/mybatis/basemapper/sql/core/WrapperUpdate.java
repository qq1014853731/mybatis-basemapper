package top.chukongxiang.mybatis.basemapper.sql.core;

/**
 * 更新Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-16 16:47:20
 */
public interface WrapperUpdate<T, E extends Wrapper<T, E, Column>, Column> extends Wrapper<T, E, Column> {
    E set(boolean condition, Column column, Object value);
    default E set(Column column, Object value) {
        return set(true, column, value);
    }

    void setTableName(String tableName);
}
