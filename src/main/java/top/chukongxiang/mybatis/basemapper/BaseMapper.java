package top.chukongxiang.mybatis.basemapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.annotation.ProviderContext;
import top.chukongxiang.mybatis.basemapper.model.Constants;
import top.chukongxiang.mybatis.basemapper.providers.MapperDeleteProvider;
import top.chukongxiang.mybatis.basemapper.providers.MapperInsertProvider;
import top.chukongxiang.mybatis.basemapper.providers.MapperSelectProvider;
import top.chukongxiang.mybatis.basemapper.providers.MapperUpdateProvider;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperQuery;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperUpdate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 09:45:30
 */
public interface BaseMapper<T> {

    /**
     * 查询所有数据
     * @see MapperSelectProvider#selectAll(ProviderContext)
     * @return 实体信息[]
     */
    @SelectProvider(type = MapperSelectProvider.class, method = "selectAll")
    List<T> selectAll();

    /**
     * 根据主键查询实体
     * @see MapperSelectProvider#selectById(ProviderContext)
     * @param id 主键
     * @return 实体
     */
    @SelectProvider(type = MapperSelectProvider.class, method = "selectById")
    T selectById(Serializable id);

    /**
     * 根据id数组查询实体列表
     *
     * @param ids id数组
     * @return 实体列表
     * @see MapperSelectProvider#selectByIds(ProviderContext)
     */
    @SelectProvider(type = MapperSelectProvider.class, method = "selectByIds")
    List<T> selectByIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> ids);

    /**
     * 根据id数组查询实体列表
     *
     * @param ids id数组
     * @return 实体列表
     * @see MapperSelectProvider#selectByIds(ProviderContext)
     */
    @SelectProvider(type = MapperSelectProvider.class, method = "selectByIds")
    List<T> selectByIdsArr(@Param(Constants.COLLECTION) Serializable[] ids);

    /**
     * 查询单个实体
     * @param wrapper 实体信息
     * @return 实体列表
     * @see MapperSelectProvider#selectWrapper(ProviderContext, Map)
     */
    @SelectProvider(type = MapperSelectProvider.class, method = "selectWrapper")
    <E extends WrapperQuery<T, E, Column>, Column> T selectOne(@Param(Constants.WRAPPER) WrapperQuery<T, E, Column> wrapper);

    /**
     * 查询实体列表
     * @param wrapper 实体信息
     * @return 实体列表
     * @see MapperSelectProvider#selectWrapper(ProviderContext, Map)
     */
    @SelectProvider(type = MapperSelectProvider.class, method = "selectWrapper")
    <E extends WrapperQuery<T, E, Column>, Column> List<T> selectList(@Param(Constants.WRAPPER) WrapperQuery<T, E, Column> wrapper);

    /**
     * 查询总计数，忽略SELECT的列
     * @param wrapper SQL条件构造
     * @return
     * @see MapperSelectProvider#countWrapper(ProviderContext, Map)
     */
    @SelectProvider(type = MapperSelectProvider.class, method = "countWrapper")
    <E extends WrapperQuery<T, E, Column>, Column> int count(@Param(Constants.WRAPPER) WrapperQuery<T, E, Column> wrapper);

    /**
     * 根据id更新实体
     * @param entity 实体
     * @return 影响行数
     * @see MapperUpdateProvider#updateById(ProviderContext)
     */
    @UpdateProvider(type = MapperUpdateProvider.class, method = "updateById")
    int updateById(T entity);

    /**
     * 根据构造器更新数据
     * @param wrapper 构造器
     * @return 影响行数
     * @see MapperUpdateProvider#updateWrapper(ProviderContext, Map)
     */
    @UpdateProvider(type = MapperUpdateProvider.class, method = "updateWrapper")
    <E extends WrapperUpdate<T, E, Column>, Column> int update(@Param(Constants.WRAPPER) WrapperUpdate<T, E, Column> wrapper);

    /**
     * 插入实体
     * @param entity 实体信息
     * @return 影响行数
     * @see MapperInsertProvider#insert(ProviderContext, Map)
     */
    @InsertProvider(type = MapperInsertProvider.class, method = "insert")
    @Options(useGeneratedKeys = true)
    int insert(@Param(Constants.ENTITY) T entity);

    /**
     * 插入或更新
     * @param entity 实体信息
     * @return 影响行数
     * @see MapperInsertProvider#insertOrUpdate(ProviderContext, Map)
     */
    @InsertProvider(type = MapperInsertProvider.class, method = "insertOrUpdate")
    @Options(useGeneratedKeys = true)
    int insertOrUpdate(@Param(Constants.ENTITY) T entity);

    /**
     * 批量插入
     * @param entities 实体
     * @return 影响行数
     * @see MapperInsertProvider#batchInsert(ProviderContext, Map)
     */
    @InsertProvider(type = MapperInsertProvider.class, method = "batchInsert")
    @Options(useGeneratedKeys = true)
    int batchInsert(@Param(Constants.COLLECTION) List<T> entities);

    /**
     * 批量插入或更新
     * @param entities 实体
     * @return 影响行数
     * @see MapperInsertProvider#batchInsertOrUpdate(ProviderContext, Map)
     */
    @InsertProvider(type = MapperInsertProvider.class, method = "batchInsertOrUpdate")
    @Options(useGeneratedKeys = true)
    int batchInsertOrUpdate(@Param(Constants.COLLECTION) List<T> entities);

    /**
     * 锁表批量插入或更新
     * @param entities 实体
     * @return 影响行数
     * @see MapperInsertProvider#lockBatchInsertOrUpdate(ProviderContext, Map)
     */
    @InsertProvider(type = MapperInsertProvider.class, method = "batchInsertOrUpdate")
    @Options(useGeneratedKeys = true)
    int lockBatchInsertOrUpdate(@Param(Constants.COLLECTION) List<T> entities);

    /**
     * 根据主键删除数据
     * @param id 主键值
     * @return 影响行数
     * @see MapperDeleteProvider#deleteById(ProviderContext)
     */
    @DeleteProvider(type = MapperDeleteProvider.class, method = "deleteById")
    int deleteById(Serializable id);

    /**
     * 根据主键删除数据
     * @param ids 主键值
     * @return 影响行数
     * @see MapperDeleteProvider#deleteByIds(ProviderContext)
     */
    @DeleteProvider(type = MapperDeleteProvider.class, method = "deleteByIds")
    int deleteByIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> ids);

    /**
     * 根据主键删除数据
     * @param ids 主键值
     * @return 影响行数
     * @see MapperDeleteProvider#deleteByIdsArr(ProviderContext)
     */
    @DeleteProvider(type = MapperDeleteProvider.class, method = "deleteByIdsArr")
    int deleteByIdsArr(@Param(Constants.COLLECTION) Serializable... ids);

    /**
     * 根据构造器条件删除数据
     * @param wrapper 构造器
     * @return 影响行数
     * @see MapperDeleteProvider#deleteWrapper(ProviderContext, Map)
     */
    @DeleteProvider(type = MapperDeleteProvider.class, method = "deleteWrapper")
    <E extends WrapperQuery<T, E, Column>, Column> int delete(@Param(Constants.WRAPPER) WrapperQuery<T, E, Column> wrapper);
}
