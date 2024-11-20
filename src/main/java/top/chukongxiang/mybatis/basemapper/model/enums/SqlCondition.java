package top.chukongxiang.mybatis.basemapper.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-08 14:12:20
 */
@Getter
@AllArgsConstructor
public enum SqlCondition {

    /**
     * column = #{value}
     */
    EQ("= #{", "}"),

    /**
     * column != #{value}
     */
    NE("!= #{", "}"),

    /**
     * column LIKE CONCAT('%', #{value}, '%')
     */
    LIKE("LIKE CONCAT('%', #{", "}, '%')"),
    NOT_LIKE("NOT LIKE CONCAT('%', #{", "}, '%')"),

    /**
     * column LIKE CONCAT('%', #{value})
     */
    LIKE_LEFT("LIKE CONCAT('%', #{", "})"),
    NOT_LIKE_LEFT("NOT LIKE CONCAT('%', #{", "})"),

    /**
     * column LIKE CONCAT(#{value}, '%')
     */
    LIKE_RIGHT("LIKE CONCAT(#{", "}, '%')"),
    NOT_LIKE_RIGHT("NOT LIKE CONCAT(#{", "}, '%')"),

    /**
     * column > #{value}
     */
    GT("> #{", "}"),

    /**
     * column >= #{value}
     */
    GE(">= #{", "}"),

    /**
     * column < #{value}
     */
    LT("< #{", "}"),

    /**
     * column <= #{value}
     */
    LE("<= #{", "}"),
    ;
    private final String prefix;

    private final String suffix;

}
