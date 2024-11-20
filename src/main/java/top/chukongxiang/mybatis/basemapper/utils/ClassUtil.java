package top.chukongxiang.mybatis.basemapper.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-20 16:11:59
 */
public class ClassUtil extends cn.hutool.core.util.ClassUtil {

    private static final Map<String, Boolean> HAS_CLASS = new ConcurrentHashMap<>();

    public static boolean hasClass(String className) {
        return HAS_CLASS.computeIfAbsent(className, cn -> {
            try {
                Class.forName(className);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        });
    }

}
