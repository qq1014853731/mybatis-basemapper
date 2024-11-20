package top.chukongxiang.mybatis.basemapper.sql;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
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
 * 列类型是lambda表达式的查询Wrapper
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-14 16:15:47
 */
public class LambdaQueryWrapper<T>
        extends AbstractWrapper<T, LambdaQueryWrapper<T>, Func1<T, ?>>
        implements WrapperLambda<T, LambdaQueryWrapper<T>>, WrapperQuery<T, LambdaQueryWrapper<T>, Func1<T, ?>> {

    @Setter
    @Getter
    private String tableName;
    @Getter
    private String sqlSelect = "*";
    @Getter
    private final List<OrderBy> orderBys = new ArrayList<>();
    private List<String> groupBys;
    private String havingSql = "";
    private List<Object> havingValues;
    private String lastSql;

    public LambdaQueryWrapper() {
        super(null, null);
    }

    public LambdaQueryWrapper(T entity) {
        super(entity, (Class<T>) entity.getClass());
    }

    public LambdaQueryWrapper(Class<T> entityClass) {
        super(entityClass);
        if (entityClass != null) {
            TableMetadata<T> tableMetadata = TableMetadata.forClass(entityClass);
            if (tableMetadata != null) {
                this.tableName = tableMetadata.getTableName();
            }
        }
    }

    @SafeVarargs
    @Override
    public final LambdaQueryWrapper<T> select(boolean condition, boolean distinct, Func1<T, ?>... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            this.sqlSelect = Arrays.stream(columns).map(this::getColumn).collect(Collectors.joining(", "));
        }
        if (distinct) {
            this.sqlSelect = "DISTINCT " + this.sqlSelect;
        }
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaQueryWrapper<T> groupBy(boolean condition, Func1<T, ?>... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            this.groupBys = Arrays.stream(columns).map(this::getColumn).collect(Collectors.toList());
        }
        return this;
    }

    @Override
    public <E extends Wrapper<T, E, Func1<T, ?>>> LambdaQueryWrapper<T> having(boolean condition, Consumer<Wrapper<T, E, Func1<T, ?>>> consumer) {
        if (condition && consumer != null) {
            AbstractWrapper<T, E, Func1<T, ?>> havingWrapper = new AbstractWrapper<T, E, Func1<T, ?>>(this.entityClass) {};
            consumer.accept(havingWrapper);
            String havingsql = havingWrapper.getWhereSql();
            if (StrUtil.isNotBlank(havingsql)) {
                this.havingSql = havingsql;
                this.havingValues = havingWrapper.getValues();
            }
        }
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaQueryWrapper<T> orderByAsc(boolean condition, Func1<T, ?>... columns) {
        return orderBy(condition, true, columns);
    }

    @SafeVarargs
    @Override
    public final LambdaQueryWrapper<T> orderByDesc(boolean condition, Func1<T, ?>... columns) {
        return orderBy(condition, false, columns);
    }

    @SafeVarargs
    @Override
    public final LambdaQueryWrapper<T> orderBy(boolean condition, boolean isAsc, Func1<T, ?>... columns) {
        if (condition && ArrayUtil.isNotEmpty(columns)) {
            orderBys.addAll(
                    Arrays.stream(columns).map(this::getColumn)
                            .map(column -> QueryWrapper.OrderBy.of(column).setAsc(isAsc))
                            .collect(Collectors.toList()));
        }
        return this;
    }

    @Override
    public LambdaQueryWrapper<T> last(boolean condition, String sql) {
        if (condition) {
            this.lastSql = sql;
        }
        return this;
    }

    public QueryWrapper<T> query() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        BeanUtil.copyProperties(this, queryWrapper);
        return queryWrapper;
    }

    @Override
    public SQLInfo<T> build() {

        if (StrUtil.isBlank(this.tableName)) {
            if (this.entityClass != null) {
                this.tableName = TableMetadata.forClass(this.entityClass).getTableName();
            }
        }

        Assert.notBlank(this.tableName);

        SQL builder = new SQL()
                .SELECT(this.getSqlSelect())
                .FROM(this.tableName);

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
        String havingSql = this.havingSql;
        if (StrUtil.isNotBlank(havingSql)) {
            // 重新处理havingSQL
            int startIndex = 0;
            for (int i = 0; i < havingValues.size(); i++) {
                String src = getWrap(i);
                String dest = getWrap(super.paramIndex++);
                String tmpStr = havingSql.substring(startIndex);
                int index = tmpStr.indexOf(src);
                tmpStr = tmpStr.replace(src, dest);
                havingSql = havingSql.substring(0, startIndex) + tmpStr;
                startIndex = startIndex + index + (dest.length() - src.length());
                super.getValues().add(havingValues.get(i));
            }
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
