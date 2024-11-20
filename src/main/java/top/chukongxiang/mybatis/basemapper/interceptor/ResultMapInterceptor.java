package top.chukongxiang.mybatis.basemapper.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import top.chukongxiang.mybatis.basemapper.BaseMapper;
import top.chukongxiang.mybatis.basemapper.providers.TableMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 针对BaseMapper自动生成ResultMap
 * @author 楚孔响
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class ResultMapInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!(invocation.getTarget() instanceof Executor)) {
            return invocation.proceed();
        }
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];

        // xml sql 不做处理
        if (StrUtil.containsIgnoreCase(ms.getResource(), ".xml")) {
            return invocation.proceed();
        }
        // 是否是BaseMapper
        String mapperLocation = StrUtil.subBefore(ms.getId(), ".", true);
        if (StrUtil.isBlank(mapperLocation)) {
            return invocation.proceed();
        }
        Class<?> mapperClass;
        try {
            mapperClass = ClassUtil.loadClass(mapperLocation);
        } catch (Exception e) {
            return invocation.proceed();
        }
        if (!BaseMapper.class.isAssignableFrom(mapperClass)) {
            return invocation.proceed();
        }

        // 是否是BaseMapper中的方法(兼容自定义statement例如pageHelper)
        boolean isMapperMethod = false;
        String methodName = StrUtil.subAfter(ms.getId(), ".", true);
        for (Method method : ReflectUtil.getMethods(mapperClass)) {
            if (Objects.equals(method.getName(), methodName)) {
                isMapperMethod = true;
                break;
            }
        }
        if (!isMapperMethod) {
            return invocation.proceed();
        }

        ResultMap resultMap = ms.getResultMaps().iterator().next();
        if (!CollUtil.isEmpty(resultMap.getResultMappings())) {
            return invocation.proceed();
        }
        Class<?> mapType = resultMap.getType();
        if (Collection.class.isAssignableFrom(mapType)) {
            return invocation.proceed();
        }
        TableMetadata<?> tableMetadata = TableMetadata.forClass(mapType);
        Map<Field, String> fieldColumnMap = tableMetadata.getFieldColumnMap();

        List<ResultMapping> resultMappings = new ArrayList<>(fieldColumnMap.size());
        for (Field field : fieldColumnMap.keySet()) {
            String property = field.getName();
            String column = fieldColumnMap.get(field);
            ResultMapping resultMapping = new ResultMapping.Builder(ms.getConfiguration(), property, column, field.getType()).build();
            resultMappings.add(resultMapping);
        }

        ResultMap newRm = new ResultMap.Builder(ms.getConfiguration(), resultMap.getId(), mapType, resultMappings).build();

        ReflectUtil.setFieldValue(ms, "resultMaps", newRm);

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}