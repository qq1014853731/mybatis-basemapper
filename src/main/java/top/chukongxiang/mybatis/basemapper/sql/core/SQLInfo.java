package top.chukongxiang.mybatis.basemapper.sql.core;

import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import top.chukongxiang.mybatis.basemapper.model.Constants;
import top.chukongxiang.mybatis.basemapper.utils.SqlUtil;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Wrapper构造器构造结果
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-14 19:21:36
 */
@Data
@Accessors(chain = true)
@Builder
public final class SQLInfo<T> {

    @Tolerate
    public SQLInfo() {}

    private Class<T> entityClass;

    private String sql;

    private List<Object> values;


    /**
     * 从sql中获取Where后的所有条件信息
     * @return where后的所有条件
     */
    public String getCondition() {
        if (StrUtil.isBlank(this.sql)) {
            return "";
        }
        Matcher matcher = Constants.WHERE_PAT.matcher(SqlUtil.normalSql(this.sql));
        if (matcher.find()) {
            return matcher.group("where");
        }
        return "";
    }

}
