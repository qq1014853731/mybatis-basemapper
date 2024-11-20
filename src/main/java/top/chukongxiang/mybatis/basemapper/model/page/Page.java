package top.chukongxiang.mybatis.basemapper.model.page;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-20 08:46:05
 */
@Data
@Accessors(chain = true)
public class Page<T> implements IPage<T> {

    private long current = 1;
    private long size = 10;
    private long total = 0;
    private List<OrderItem> orderItems = new ArrayList<>();
    private List<T> records = new ArrayList<>();

    @Override
    public List<OrderItem> orders() {
        return orderItems;
    }
}
