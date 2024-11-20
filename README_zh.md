### 基于mybatis扩展出的Wrapper
使用Mybatis官方Api实现，性能优于MybatisPlus，但功能没有MybatisPlus丰富

1. 使用方法
   1. 在SqlSessionFactoryBean创建之间代理addMapper实现id回填并加入自动映射ResultMap插件
    ```java
    import org.springframework.context.annotation.Configuration;
    import com.fz.fbm.framework.mybatis.interceptor.ResultMapInterceptor;
    import com.fz.fbm.framework.mybatis.providers.AbstractMapperProvider;
    import org.apache.ibatis.builder.xml.XMLConfigBuilder;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.mybatis.spring.SqlSessionFactoryBean;
    import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
    // 省略导入...
   
    @Configuration 
    public class MybatisConfig {
        public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
                                                   MybatisProperties mybatisProperties) {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource);
            String configLocation = mybatisProperties.getConfigLocation();
            Resource configLocationResource = new DefaultResourceLoader().getResource(configLocation);
            XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(configLocationResource.getInputStream(), null, null);
            org.apache.ibatis.session.Configuration configuration = xmlConfigBuilder.getConfiguration();
            // 注册拦截器
            configuration.addInterceptor(new ResultMapInterceptor());
            // 代理id回填
            AbstractMapperProvider.invoke(configuration);
            sessionFactory.setConfiguration(configuration);
            return sessionFactory.getObject();
        }
    }
    ```
    2. 剩余用法基本和MybatisPlus类同