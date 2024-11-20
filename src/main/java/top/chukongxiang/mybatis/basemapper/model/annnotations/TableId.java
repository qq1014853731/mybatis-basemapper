package top.chukongxiang.mybatis.basemapper.model.annnotations;

import top.chukongxiang.mybatis.basemapper.model.enums.IdType;

import java.lang.annotation.*;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 09:50:05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@TableField
public @interface TableId {

    /**
     * 字段名（该值可无）
     */
    String value() default "";

    /**
     * 主键类型
     * {@link IdType}
     */
    IdType type() default IdType.NONE;

}
