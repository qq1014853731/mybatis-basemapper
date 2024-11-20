package top.chukongxiang.mybatis.basemapper.sql.core;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import top.chukongxiang.mybatis.basemapper.providers.TableMetadata;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 查询Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-16 16:43:52
 */
public interface WrapperQuery<T, E extends Wrapper<T, E, Column>, Column> extends Wrapper<T, E, Column> {

    /**
     * SELECT DISTINCT column[0], column[1]...
     * @param condition 条件
     * @param distinct distinct
     * @param columns 列
     * @return Wrapper
     */
    E select(boolean condition, boolean distinct, Column... columns);

    /**
     * SELECT column[0], column[1]...
     * @param condition 条件
     * @param columns 列
     * @return Wrapper
     */
    default E select(boolean condition, Column... columns) {
        return select(condition, false, columns);
    }

    /**
     * SELECT column[0], column[1]...
     * @param columns 列
     * @return Wrapper
     */
    default E select(Column... columns) {
        return select(true, columns);
    }

    /**
     * group by column[0],column[1]...
     * @param condition
     * @param columns
     * @return
     */
    E groupBy(boolean condition, Column... columns);
    default E groupBy(Column... columns) {
        return groupBy(true, columns);
    }

    /**
     * having $wrapper
     * @param condition
     * @param consumer
     * @return
     * @param <F>
     */
    <F extends Wrapper<T, F, Column>> E having(boolean condition, Consumer<Wrapper<T, F, Column>> consumer);
    default <F extends Wrapper<T, F, Column>> E having(Consumer<Wrapper<T, F, Column>> consumer) {
        return having(true, consumer);
    }

    /**
     * order by column[0] ASC, column[1] ASC ,...
     * @param condition
     * @param columns
     * @return
     */
    E orderByAsc(boolean condition, Column... columns);
    default E orderByAsc(Column... columns) {
        return orderByAsc(true, columns);
    }

    /**
     * order by column[0] DESC, column[1] DESC ,...
     * @param condition
     * @param columns
     * @return
     */
    E orderByDesc(boolean condition, Column... columns);
    default E orderByDesc(Column... columns) {
        return orderByDesc(true, columns);
    }

    /**
     * order by
     * @param condition 是否执行
     * @param isAsc 是否正序
     * @param columns 排序列
     * @return
     */
    E orderBy(boolean condition, boolean isAsc, Column... columns);
    default E orderBy(boolean isAsc, Column... columns) {
        return orderBy(true, isAsc, columns);
    }

    /**
     * sql 最后拼接
     * @param sql 拼接sql
     * @return
     */
    E last(boolean condition, String sql);
    default E last(String sql) {
        return last(true, sql);
    }

    /**
     * 修改表名
     * @param tableName
     */
    void setTableName(String tableName);

    /**
     * 获取表名
     * @return
     */
    String getTableName();

    /**
     * 排序规则
     */
    List<OrderBy> getOrderBys();

    /**
     * 默认查询列
     * @return
     */
    default String getSqlSelect() {
        Class<T> entityClass = this.getEntityClass();
        if (entityClass != null) {
            TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
            Collection<String> columns = tableMetadata.getFieldWrappedCoulmnMap().values();
            return String.join(", ", columns);
        }
        return "*";
    }

    @Data
    @Accessors(chain = true)
    @RequiredArgsConstructor
    class OrderBy {
        protected final String column;
        protected boolean isAsc = true;

        public static OrderBy of(String column) {
            return new OrderBy(column);
        }
    }


}
