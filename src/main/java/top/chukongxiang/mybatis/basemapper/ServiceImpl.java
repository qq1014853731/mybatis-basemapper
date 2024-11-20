package top.chukongxiang.mybatis.basemapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.chukongxiang.mybatis.basemapper.model.page.IPage;
import top.chukongxiang.mybatis.basemapper.model.page.OrderItem;
import top.chukongxiang.mybatis.basemapper.sql.Wrappers;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperQuery;
import top.chukongxiang.mybatis.basemapper.sql.core.WrapperUpdate;
import top.chukongxiang.mybatis.basemapper.utils.ClassUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-10-31 14:56:08
 */
@Slf4j
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T>{

    @Autowired
    protected M baseMapper;

    @Getter
    @Setter
    private int batchSize = 200;

    @Override
    public M getBaseMapper() {
        Assert.notNull(this.baseMapper, "baseMapper can not be null");
        return this.baseMapper;
    }

    @Override
    public List<T> listAll() {
        return baseMapper.selectAll();
    }

    @Override
    public T getById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> ids) {
        return baseMapper.selectByIds(ids);
    }

    @Override
    public List<T> listByIds(Serializable... ids) {
        return baseMapper.selectByIdsArr(ids);
    }

    @Override
    public <E extends WrapperQuery<T, E, Column>, Column> List<T> list(@Param("eq") WrapperQuery<T, E, Column> wrapper) {
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<T> list(T entity) {
        return baseMapper.selectList(Wrappers.query(entity));
    }

    @Override
    public IPage<T> page(IPage<T> page) {
        return page(page, Wrappers.emptyWrapper());
    }

    @Override
    public <E extends WrapperQuery<T, E, Column>, Column> IPage<T> page(IPage<T> page, WrapperQuery<T, E, Column> wrapper) {
        List<WrapperQuery.OrderBy> wrapperOrderBys = wrapper.getOrderBys();
        List<OrderItem> pageOrderBys = page.orders();

        String orderBySql = "";
        // 优先使用wrapper排序
        if (CollUtil.isNotEmpty(wrapperOrderBys)){
            orderBySql = orderBySql + wrapperOrderBys.stream()
                    .map(orderBy -> orderBy.getColumn() + (orderBy.isAsc() ? " ASC" : " DESC"))
                    .collect(Collectors.joining(", "));
        }
        // 再使用page参数排序
        if (CollUtil.isNotEmpty(pageOrderBys)) {
            orderBySql = orderBySql + pageOrderBys.stream()
                    .map(orderBy -> orderBy.getColumn() + (orderBy.isAsc() ? " ASC" : " DESC"))
                    .collect(Collectors.joining(", "));
        }
        // 兼容PageHelper
        if (StrUtil.isBlank(orderBySql)) {
            orderBySql = null;
        }

        // 为了减少service臃肿，减少mybatis插件，这里直接使用PageHelper分页
        int current = (int) page.getCurrent();
        int size = (int) page.getSize();
        IPage<T> rsPage = new top.chukongxiang.mybatis.basemapper.model.page.Page<>();
        rsPage.setCurrent(current);
        rsPage.setSize(size);
        if (ClassUtil.hasClass("com.github.pagehelper.PageHelper")) {
            // PageHelper分页
            try (Page<T> rs = PageHelper.startPage(current, size, orderBySql)) {
                list(wrapper);
                rsPage.setRecords(rs.getResult());
                rsPage.setTotal(rs.getTotal());
            }
        } else {
            int count = count(wrapper);
            List<T> rs = list(wrapper);
            rsPage.setRecords(rs);
            rsPage.setTotal(count);
        }
        return rsPage;
    }

    @Override
    public int count() {
        return baseMapper.count(Wrappers.emptyWrapper());
    }

    @Override
    public <E extends WrapperQuery<T, E, Column>, Column> int count(WrapperQuery<T, E, Column> wrapper) {
        return baseMapper.count(wrapper);
    }

    @Override
    public <E extends WrapperQuery<T, E, Column>, Column> T getOne(WrapperQuery<T, E, Column> wrapper) {
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public T getOne(T entity) {
        return baseMapper.selectOne(Wrappers.query(entity));
    }

    @Override
    public boolean save(T entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        int row = baseMapper.insertOrUpdate(entity);
        return row > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSave(List<T> entities) {
        int lines = 0;
        for (List<T> items : ListUtil.split(entities, batchSize)) {
            lines += baseMapper.batchInsert(items);
        }
        return lines > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveOrUpdate(List<T> entities) {
        int lines = 0;
        for (List<T> items : ListUtil.split(entities, batchSize)) {
            lines += baseMapper.batchInsertOrUpdate(items);
        }
        return lines > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockBatchSaveOrUpdate(List<T> entities) {
        int lines = 0;
        for (List<T> items : ListUtil.split(entities, batchSize)) {
            lines += baseMapper.lockBatchInsertOrUpdate(items);
        }
        return lines > 0;
    }

    @Override
    public boolean updateById(T entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public <E extends WrapperUpdate<T, E, Column>, Column> boolean update(WrapperUpdate<T, E, Column> wrapper) {
        return baseMapper.update(wrapper) > 0;
    }

    @Override
    public boolean removeById(Serializable id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean removeByIds(Serializable... ids) {
        return baseMapper.deleteByIdsArr(ids) > 0;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public <E extends WrapperQuery<T, E, Column>, Column> boolean remove(WrapperQuery<T, E, Column> wrapper) {
        return baseMapper.delete(wrapper) > 0;
    }
}
