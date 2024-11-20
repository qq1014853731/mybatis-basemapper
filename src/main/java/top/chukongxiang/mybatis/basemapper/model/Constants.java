package top.chukongxiang.mybatis.basemapper.model;

import top.chukongxiang.mybatis.basemapper.sql.core.WrapperQuery;

import java.util.regex.Pattern;

/**
 * @author 楚孔响
 * @version 1.0.0
 * @date 2024-11-15 11:25:20
 */
public interface Constants {

    Pattern WHERE_PAT = Pattern.compile("(?is)\\s+WHERE\\s+(?<where>.*)");

    /**
     * <p>sql构造器参数名</p>
     * <p>可将Wrapper用于自定义xml中，但要使用该字段生成名参数名，并将生成后的sql放在xml中</p>
     * <p>示例：</p>
     * <pre>
     *     interface EntityMapper {
     *         &lt;W extends Wrapper&lt;Entity, W, Column&gt;, Column&gt; List&lt;Entity&gt; select(@Param(Constants.WRAPPER)WrapperQuery&lt;Entity, QueryWrapper&lt;Entity, W, Column&gt;&gt; wrapper);
     *     }
     *
     *
     *     xxx.xml
     *     &lt;select id="select"&gt;
     *         SELECT * FROM table
     *         &lt;where&gt;
     *             ${ew.build().getCondition()}
     *         &lt;/where&gt;
     *     &lt;/select&gt;
     * </pre>
     * Mapper示例可跳转至：
     * @see com.fz.fbm.framework.mybatis.BaseMapper#selectList(WrapperQuery)
     * @see com.fz.fbm.framework.mybatis.BaseMapper#selectOne(WrapperQuery)
     */
    String WRAPPER = "ew";
    String COLLECTION = "coll";
    String ENTITY = "et";

    String LIMIT = "LIMIT";
    String LIMIT_1 = LIMIT + " 1";

}
