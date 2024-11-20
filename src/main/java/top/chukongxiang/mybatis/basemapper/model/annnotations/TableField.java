package top.chukongxiang.mybatis.basemapper.model.annnotations;

import top.chukongxiang.mybatis.basemapper.model.enums.FieldStrategy;
import top.chukongxiang.mybatis.basemapper.model.enums.SqlCondition;
import top.chukongxiang.mybatis.basemapper.model.enums.WrapType;

import java.lang.annotation.*;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 09:52:35
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableField {

    /**
     * 数据库字段值
     * <p>
     * 不需要配置该值的情况:
     * <li> 当 {@link TableField#mapUnderlineCase()} 为 true 时,
     * (mp下默认是true,mybatis默认是false), 数据库字段值.replace("_","").toUpperCase() == 实体属性名.toUpperCase() </li>
     * <li> 当 {@link TableField#mapUnderlineCase()} 为 false 时,
     * 数据库字段值.toUpperCase() == 实体属性名.toUpperCase() </li>
     */
    String value() default "";

    /**
     * 是否为数据库表字段
     * <p>
     * 默认 true 存在，false 不存在
     */
    boolean exist() default true;


    /**
     * 字段 update set 部分注入, 该注解优于 el 注解使用
     * <p>
     * 例1：@TableField(.. , update="%s+1") 其中 %s 会填充为字段
     * 输出 SQL 为：update 表 set 字段=字段+1 where ...
     * <p>
     * 例2：@TableField(.. , update="now()") 使用数据库时间
     * 输出 SQL 为：update 表 set 字段=now() where ...
     */
    String update() default "";

    /**
     * 字段验证策略之 insert: 当insert操作时，该字段拼接insert语句时的策略
     * <p>
     * IGNORED: 直接拼接 insert into table_a(column) values (#{columnProperty});
     * NOT_NULL: insert into table_a(<if test="columnProperty != null">column</if>) values (<if test="columnProperty != null">#{columnProperty}</if>)
     * NOT_EMPTY: insert into table_a(<if test="columnProperty != null and columnProperty!=''">column</if>) values (<if test="columnProperty != null and columnProperty!=''">#{columnProperty}</if>)
     * NOT_EMPTY 如果针对的是非 CharSequence 类型的字段则效果等于 NOT_NULL
     *
     * @since 3.1.2
     */
    FieldStrategy insertStrategy() default FieldStrategy.DEFAULT;

    /**
     * 字段验证策略之 update: 当更新操作时，该字段拼接set语句时的策略
     * <p>
     * IGNORED: 直接拼接 update table_a set column=#{columnProperty}, 属性为null/空string都会被set进去
     * NOT_NULL: update table_a set <if test="columnProperty != null">column=#{columnProperty}</if>
     * NOT_EMPTY: update table_a set <if test="columnProperty != null and columnProperty!=''">column=#{columnProperty}</if>
     * NOT_EMPTY 如果针对的是非 CharSequence 类型的字段则效果等于 NOT_NULL
     *
     * @since 3.1.2
     */
    FieldStrategy updateStrategy() default FieldStrategy.DEFAULT;

    /**
     * 字段验证策略之 where: 表示该字段在拼接where条件时的策略
     * <p>
     * IGNORED: 直接拼接 column=#{columnProperty}
     * NOT_NULL: <if test="columnProperty != null">column=#{columnProperty}</if>
     * NOT_EMPTY: <if test="columnProperty != null and columnProperty!=''">column=#{columnProperty}</if>
     * NOT_EMPTY 如果针对的是非 CharSequence 类型的字段则效果等于 NOT_NULL
     *
     * @since 3.1.2
     */
    FieldStrategy whereStrategy() default FieldStrategy.DEFAULT;

    /**
     * 生成ResultMap时是否自动映射大写到下划线，仅当没有配置字段名称时才生效
     * @return 布尔值
     */
    boolean mapUnderlineCase() default true;

    /**
     * 表名前后包装，例：`table_name`
     * @return 类型
     */
    WrapType wrapType() default WrapType.AUTO;

    /**
     * 包装字符串，例：`
     * @return 包装字符
     */
    String wrap() default "";

    /**
     * 生成where时使用的条件
     * @return
     */
    SqlCondition condition() default SqlCondition.EQ;

    /**
     * 是否加入select查询列
     * @return
     */
    boolean select() default true;

}
