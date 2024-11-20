package top.chukongxiang.mybatis.basemapper.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-13 14:43:02
 */
@Getter
@AllArgsConstructor
public enum WrapperSqlCondition {
    EQ("="),
    NE("!="),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),

    /**
     * like %value%
     */
    LIKE("LIKE"),

    /**
     * not like %value%
     */
    NOT_LIKE("NOT LIKE"),

    /**
     * like %value
     */
    LIKE_LEFT("LIKE"),

    /**
     * not like %value
     */
    NOT_LIKE_LEFT("NOT LIKE"),

    /**
     * like value%
     */
    LIKE_RIGHT("LIKE"),

    /**
     * not like value%
     */
    NOT_LIKE_RIGHT("NOT LIKE"),

    /**
     * IN (values...)
     */
    IN("IN"),
    IN_QUERY("IN"),

    /**
     * NOT IN (values....)
     */
    NOT_IN("NOT IN"),
    NOT_IN_QUERY("NOT IN"),

    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),

    /**
     * is null
     */
    IS_NULL("IS NULL"),

    /**
     * is not null
     */
    IS_NOT_NULL("IS NOT NULL"),

    /**
     * BETWEEN #{start} AND #{end}
     */
    BETWEEN("BETWEEN"),

    /**
     * NOT BETWEEN #{start} AND #{end}
     */
    NOT_BETWEEN("NOT BETWEEN"),
    ;
    private final String value;
}
