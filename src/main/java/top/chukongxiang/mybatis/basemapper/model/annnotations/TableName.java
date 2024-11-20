package top.chukongxiang.mybatis.basemapper.model.annnotations;

import top.chukongxiang.mybatis.basemapper.model.enums.WrapType;

import java.lang.annotation.*;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 09:48:01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface TableName {

    /**
     * 显式指定表名称。
     */
    String value();

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

}
