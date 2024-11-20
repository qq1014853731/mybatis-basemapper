package top.chukongxiang.mybatis.basemapper.utils;

import org.apache.ibatis.jdbc.SQL;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-20 09:51:24
 */
public class SqlUtil {

    public static String normalSql(SQL sql, String... lastSqls) {
        return normalSql(sql.toString() + String.join(" ", lastSqls));
    }
    public static String normalSql(String sql) {
        return sql.replaceAll("\\s+", " ");
    }

}
