package top.chukongxiang.mybatis.basemapper.sql;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.jdbc.SQL;
import top.chukongxiang.mybatis.basemapper.providers.TableMetadata;
import top.chukongxiang.mybatis.basemapper.sql.core.*;
import top.chukongxiang.mybatis.basemapper.utils.SqlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 列类型是字符串的查询Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-13 09:31:44
 */
public class QueryWrapper<T>
        extends AbstractWrapper<T, QueryWrapper<T>, String>
        implements WrapperQuery<T, QueryWrapper<T>, String>, WrapperString<T, QueryWrapper<T>> {

    @Setter
    @Getter
    private String tableName;
    @Getter
    private String sqlSelect = "*";
    @Getter
    private final List<OrderBy> orderBys = new ArrayList<>();
    private List<String> groupBys;
    private String havingSql;
    private List<Object> havingValues;
    private String lastSql;

    public QueryWrapper() {
        this(null, null);
    }

    public QueryWrapper(T entity) {
        this(entity, (Class<T>) entity.getClass());
    }

    public QueryWrapper(Class<T> entityClass) {
        this(null, entityClass);
    }

    private QueryWrapper(T entity, Class<T> entityClass) {
        super(entity, entityClass);
        if (entityClass != null) {
            TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
            if (tableMetadata != null) {
                this.tableName = tableMetadata.getTableName();
            }
        }
    }

    @Override
    public QueryWrapper<T> select(boolean condition, boolean distinct, String... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            this.sqlSelect = Arrays.stream(columns).map(this::getColumn).collect(Collectors.joining(", "));
        }
        if (distinct) {
            this.sqlSelect = "DISTINCT " + sqlSelect;
        }
        return this;
    }

    @Override
    public QueryWrapper<T> groupBy(boolean condition, String... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            this.groupBys = Arrays.stream(columns).map(this::getColumn).collect(Collectors.toList());
        }
        return this;
    }

    @Override
    public <E extends Wrapper<T, E, String>> QueryWrapper<T> having(boolean condition, Consumer<Wrapper<T, E, String>> consumer) {
        if (condition && consumer != null) {
            AbstractWrapper<T, E, String> havingWrapper = new AbstractWrapper<T, E, String>(this.entityClass) {};
            consumer.accept(havingWrapper);
            String havingsql = havingWrapper.getWhereSql();
            if (StrUtil.isNotBlank(havingsql)) {
                this.havingSql = havingsql;
                this.havingValues = havingWrapper.getValues();
            }
        }
        return this;
    }

    @Override
    public QueryWrapper<T> orderByAsc(boolean condition, String... columns) {
        return orderBy(condition, true, columns);
    }

    @Override
    public QueryWrapper<T> orderByDesc(boolean condition, String... columns) {
        return orderBy(condition, false, columns);
    }

    @Override
    public QueryWrapper<T> orderBy(boolean condition, boolean isAsc, String... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            orderBys.addAll(
                    Arrays.stream(columns).map(this::getColumn).map(column -> OrderBy.of(column).setAsc(isAsc)).collect(Collectors.toList()));
        }
        return this;
    }

    @Override
    public QueryWrapper<T> last(boolean condition, String sql) {
        if (condition) {
            this.lastSql = sql;
        }
        return this;
    }

    public LambdaQueryWrapper<T> lambda() {
        LambdaQueryWrapper<T> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        BeanUtil.copyProperties(this, lambdaQueryWrapper);
        return lambdaQueryWrapper;
    }

    @Override
    public SQLInfo<T> build() {

        Class<T> entityClass = this.getEntityClass();

        String tableName = this.getTableName();

        if (StrUtil.isBlank(tableName)) {
            if (entityClass != null) {
                this.setTableName(TableMetadata.forClass(entityClass).getTableName());
                tableName = this.getTableName();
            }
        }

        Assert.notBlank(tableName);

        SQL builder = new SQL()
                .SELECT(this.getSqlSelect())
                .FROM(tableName);

        // where
        String whereSql = super.getWhereSql();
        if (StrUtil.isNotBlank(whereSql)) {
            builder.WHERE(whereSql);
        }

        // group by
        if (CollUtil.isNotEmpty(groupBys)) {
            String groupBySql = String.join(", ", groupBys);
            builder.GROUP_BY(groupBySql);
        }

        // having
        if (StrUtil.isNotBlank(this.havingSql)) {
            // 重新处理havingSQL
            String havingSql = resetSql(this.havingSql, havingValues.size());
            super.getValues().addAll(havingValues);
            builder.HAVING(havingSql);
        }


        // order by
        if (CollUtil.isNotEmpty(orderBys)) {
            String orderBySql = orderBys.stream()
                    .map(orderBy -> orderBy.getColumn() + " " + (orderBy.isAsc() ? "ASC" : "DESC"))
                    .collect(Collectors.joining(", "));
            builder.ORDER_BY(orderBySql);
        }

        // last
        String lastSql = "";
        if (StrUtil.isNotBlank(this.lastSql)) {
            lastSql = " " + StrUtil.trim(this.lastSql);
        }

        return SQLInfo.<T>builder().entityClass(entityClass).sql(SqlUtil.normalSql(builder, lastSql)).values(super.getValues()).build();
    }



}
