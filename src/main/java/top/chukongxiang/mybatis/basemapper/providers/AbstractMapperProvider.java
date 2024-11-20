package top.chukongxiang.mybatis.basemapper.providers;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import lombok.SneakyThrows;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.session.Configuration;
import top.chukongxiang.mybatis.basemapper.BaseMapper;
import top.chukongxiang.mybatis.basemapper.model.annnotations.TableField;
import top.chukongxiang.mybatis.basemapper.model.enums.FieldStrategy;
import top.chukongxiang.mybatis.basemapper.model.enums.SqlCondition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 17:59:39
 */
public abstract class AbstractMapperProvider {

    /**
     * <p>缓存Mapper和实体的对应关系<p/>
     * <p>Mapper.class : Entity.class</p>
     */
    public static final Map<Class<?>, Class<?>> MAPPER_ENTITY_CACHE = new ConcurrentHashMap<>();

    /**
     * MapperClass#Method : SQL
     */
    public static final Map<String, String> SQL_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取实体类class
     * @param providerContext
     * @return
     */
    public static Class<?> entityClass(ProviderContext providerContext) {
        Class<?> mapperType = providerContext.getMapperType();
        if (BaseMapper.class.isAssignableFrom(mapperType)) {
            return entityClass((Class<? extends BaseMapper<?>>) mapperType);
        }
        throw new RuntimeException("不支持的mapper：" + mapperType.getName());
    }

    public static Class<?> entityClass(Class<? extends BaseMapper<?>> mapperClass) {
        return MAPPER_ENTITY_CACHE.computeIfAbsent(mapperClass, mapper -> (Class<?>) TypeUtil.getTypeArgument(mapper));
    }

    public static String getCachedSql(ProviderContext providerContext, Supplier<String> sqlSupplier) {
        Method method = providerContext.getMapperMethod();
        String className = providerContext.getMapperType().getName();
        String methodName = method.getName();
        List<String> params = Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());

        String key = className + "#" + methodName + "(" + String.join(", ", params + ")");
        return SQL_CACHE.computeIfAbsent(key, k -> sqlSupplier.get());
    }

    /**
     * Cglib代理MapperRegistry，在执行addMapper之前，处理BaseMapper的@Options注解，实现动态id回填
     */
    @SneakyThrows
    public static void invoke(Configuration configuration) {

        MapperRegistry mapperRegistry = configuration.getMapperRegistry();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MapperRegistry.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if ("addMapper".equals(method.getName()) &&
                    args.length == 1 &&
                    BaseMapper.class != args[0] &&
                    BaseMapper.class.isAssignableFrom((Class<?>) args[0])) {

                // 调用的addMapper，且参数是BaseMapper.class
                Class<? extends BaseMapper<?>> mapperClass = (Class<? extends BaseMapper<?>>) args[0];

                // 仅代理BaseMapper中的注解
                for (Method optionsMethod : ReflectUtil.getMethods(BaseMapper.class, m -> AnnotationUtil.hasAnnotation(m, Options.class))) {
                    Map<Class<? extends Annotation>, Annotation> declaredAnnotations = ReflectUtil.invoke(optionsMethod, "declaredAnnotations");

                    Options oldAnnotation = (Options) declaredAnnotations.get(Options.class);
                    Annotation instance = (Annotation) Proxy.newProxyInstance(Options.class.getClassLoader(), new Class[]{Options.class}, (proxy1, annotationMethod, args1) -> {
//                        System.out.println("proxy: " + mapperClass.getName() + "#" + optionsMethod.getName() + "().@Options." + annotationMethod.getName() + "()");
                        boolean useGeneratedKeys = oldAnnotation.useGeneratedKeys();
                        if (useGeneratedKeys) {
                            // 代理@Options使用key回填，代理注解方法
                            if ("keyProperty".equals(annotationMethod.getName())) {
                                Class<?> entityClass = entityClass(mapperClass);
                                TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);
                                return tableMetadata.getIdFields().stream().map(Field::getName).collect(Collectors.joining(","));
                            }
                            if ("keyColumn".equals(annotationMethod.getName())) {
                                Class<?> entityClass = entityClass(mapperClass);
                                TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);
                                Map<Field, String> fieldColumnMap = tableMetadata.getFieldColumnMap();
                                return tableMetadata.getIdFields().stream().map(fieldColumnMap::get).collect(Collectors.joining(","));
                            }
                        }
                        return annotationMethod.invoke(oldAnnotation, args1);
                    });

                    declaredAnnotations.put(Options.class, instance);
                }
            }
            return proxy.invoke(mapperRegistry, args);
        });

        MapperRegistry proxyMapperRegistry = (MapperRegistry) enhancer.create(new Class[]{Configuration.class}, new Object[]{configuration});

        ReflectUtil.setFieldValue(configuration, "mapperRegistry", proxyMapperRegistry);
    }

    public static String getProperty(Field field) {
        return getProperty(field, null);
    }

    public static String getProperty(Field field, String prefix) {
        return (StrUtil.isBlank(prefix) ? "" : StrUtil.addSuffixIfNot(prefix, ".")) + field.getName();
    }


    /**
     * 构建默认查询时的WhereSQL，不包含&lt;where&gt;标签
     * <p><b>根据实体查询替换成了wrapper，这个方法不再使用</b></p>
     * @param entityClass 实体类
     * @return SELECT WHERE XML SQL
     * @param <T> 实体类型
     */
    @Deprecated
    public static <T> String buildWhereXmlCondition(Class<T> entityClass) {
        TableMetadata<?> tableMetadata = TableMetadata.forClass(entityClass);

        StringBuilder whereSql = new StringBuilder();

        for (Field field : tableMetadata.getFields()) {

            String wrappedColumn = tableMetadata.getWrappedColumn(field);
            String property = getProperty(field);

            SqlCondition sqlCondition = getSqlCondition(field);

            FieldStrategy fieldStrategy = getWhereStrategy(field);

            switch (fieldStrategy) {
                case DEFAULT:
                case NOT_NULL: {
                    whereSql.append("<if test=\"").append(property).append(" != null\"> and ")
                            .append(wrappedColumn)
                            .append(" ")
                            .append(sqlCondition.getPrefix())
                            .append(property)
                            .append(sqlCondition.getSuffix())
                            .append("</if>");
                    break;
                }
                case NOT_EMPTY: {
                    whereSql.append("<if test=\"").append(property).append(" != null and ")
                            .append(property)
                            .append(" != ''\"> and ")
                            .append(wrappedColumn)
                            .append(" ")
                            .append(sqlCondition.getPrefix())
                            .append(property)
                            .append(sqlCondition.getSuffix())
                            .append("</if>");
                    break;
                }
                case ALWAYS: {
                    whereSql.append("and ")
                            .append(wrappedColumn)
                            .append(" ")
                            .append(sqlCondition.getPrefix())
                            .append(property)
                            .append(sqlCondition.getSuffix());
                    break;
                }
                case NEVER:
                default: {
                    break;
                }
            }

        }

        return whereSql.toString();
    }

    /**
     * 获取字段where条件策略
     * @param field 字段
     * @return where策略
     */
    public static FieldStrategy getWhereStrategy(Field field) {
        FieldStrategy fieldStrategy = FieldStrategy.DEFAULT;

        if (AnnotationUtil.hasAnnotation(field, TableField.class)) {
            fieldStrategy = AnnotationUtil.getAnnotation(field, TableField.class).whereStrategy();
        }

        if (fieldStrategy == FieldStrategy.DEFAULT) {
            // 判断是否是字符串，如果是字符串，把DEFAULT修改为NOT_EMPTY
            if (CharSequence.class.isAssignableFrom(field.getType())) {
                fieldStrategy = FieldStrategy.NOT_EMPTY;
            }
        }
        return fieldStrategy;
    }

    /**
     * 获取字段注解的 SqlCondition 默认EQ
     * @param field 字段
     * @return SqlCondition
     */
    public static SqlCondition getSqlCondition(Field field) {
        SqlCondition sqlCondition = SqlCondition.EQ;
        TableField tableField = AnnotationUtil.getAnnotation(field, TableField.class);
        if (tableField != null) {
            sqlCondition = tableField.condition();
        }
        return sqlCondition;
    }
}
