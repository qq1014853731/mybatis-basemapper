package top.chukongxiang.mybatis.basemapper.model.enums;

/**
 * 表名或字段名包装字符
 * @author chukongxiang
 */
public enum WrapType {
        /**
         * 指定包装字符
         */
        ENABLE,

        /**
         * 不包装
         */
        DISABLE,

        /**
         * 根据JDBC自动识别包装字符
         */
        AUTO
    }