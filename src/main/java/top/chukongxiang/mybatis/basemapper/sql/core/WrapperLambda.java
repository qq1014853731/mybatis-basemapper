package top.chukongxiang.mybatis.basemapper.sql.core;

import cn.hutool.core.lang.func.Func1;

/**
 * 声明列类型是Lambda
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-16 15:09:36
 */
public interface WrapperLambda<T, E extends Wrapper<T, E, Func1<T, ?>>> extends Wrapper<T, E, Func1<T, ?>> {

}
