package top.chukongxiang.mybatis.basemapper;

import org.apache.ibatis.annotations.Param;
import top.chukongxiang.mybatis.basemapper.model.page.IPage;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperQuery;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperUpdate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 14:55:32
 */
public interface IService<T> {

    /**
     * 获取Mapper
     * @return mapper
     */
    BaseMapper<T> getBaseMapper();

    /**
     * 查询所有实体
     * @return 所有实体
     */
    List<T> listAll();

    /**
     * 根据主键id获取实体
     * @param id id
     * @return 实体
     */
    T getById(Serializable id);

    /**
     * 根据多个主键获取实体列表
     * @param ids 主键列表
     * @return 实体列表
     */
    List<T> listByIds(Collection<? extends Serializable> ids);

    /**
     * 根据多个主键获取实体列表
     * @param ids 主键列表
     * @return 实体列表
     */
    List<T> listByIds(Serializable... ids);

    /**
     * 根据构造器解获取实体列表
     * @param wrapper 构造器
     * @return 实体列表
     */
    <E extends WrapperQuery<T, E, Column>, Column> List<T> list(@Param("eq") WrapperQuery<T, E, Column> wrapper);

    /**
     * 根据实体注解获取实体列表
     * @param entity 查询实体
     * @return 实体列表
     */
    List<T> list(T entity);

    /**
     * 分页查询所有数据
     * @param page 分页条件
     * @return 分页后数据
     */
    IPage<T> page(IPage<T> page);

    /**
     * 分页查询所有数据
     * @param page 分页条件
     * @param wrapper 条件构造器
     * @return 分页后数据
     */
    <E extends WrapperQuery<T, E, Column>, Column> IPage<T> page(IPage<T> page, WrapperQuery<T, E, Column> wrapper);

    /**
     * 统计计数
     * @return 数量
     */
    int count();

    /**
     * 统计计数
     * @param wrapper 条件构造器
     * @return 数量
     * @param <E>
     * @param <Column>
     */
    <E extends WrapperQuery<T, E, Column>, Column> int count(WrapperQuery<T, E, Column> wrapper);

    /**
     * 根据构造器解获取单个实体
     * @param wrapper 构造器
     * @return 实体
     */
    <E extends WrapperQuery<T, E, Column>, Column> T getOne(WrapperQuery<T, E, Column> wrapper);

    /**
     * 根据实体注解获取单个实体
     * @param entity 查询实体
     * @return 实体
     */
    T getOne(T entity);

    /**
     * 保存一个实体
     * @param entity 实体信息
     * @return 结果
     */
    boolean save(T entity);

    /**
     * 保存或更新实体
     * @param entity 实体
     * @return 结果
     */
    boolean saveOrUpdate(T entity);

    /**
     * 批量保存实体
     * @param entities 实体列表
     * @return 保存结果
     */
    boolean batchSave(List<T> entities);

    /**
     * 批量保存或更新
     * @param entities 实体列表
     * @return 保存结果
     */
    boolean batchSaveOrUpdate(List<T> entities);

    /**
     * 锁表批量保存或更新
     * @param entities 实体列表
     * @return 保存结果
     */
    boolean lockBatchSaveOrUpdate(List<T> entities);

    /**
     * 根据id更新实体
     * @param entity 实体信息
     * @return 更新结果
     */
    boolean updateById(T entity);

    /**
     * 更新数据
     * @param wrapper 构造器
     * @return 是否成功
     */
    <E extends WrapperUpdate<T, E, Column>, Column> boolean update(WrapperUpdate<T, E, Column> wrapper);

    /**
     * 根据主键删除
     * @param id 主键
     * @return 影响行数
     */
    boolean removeById(Serializable id);

    /**
     * 根据主键删除
     * @param ids 主键
     * @return 影响行数
     */
    boolean removeByIds(Serializable... ids);

    /**
     * 根据主键删除
     * @param ids 主键
     * @return 影响行数
     */
    boolean removeByIds(Collection<? extends Serializable> ids);

    /**
     * 根据实体条件删除
     * @param entity 删除条件 and
     * @return 是否成功
     */
    <E extends WrapperQuery<T, E, Column>, Column> boolean remove(WrapperQuery<T, E, Column> wrapper);
}
