package org.dromara.business.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.business.domain.BizReproduceConfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 视频复刻配置预设视图对象 biz_reproduce_config
 */
@Data
@AutoMapper(target = BizReproduceConfig.class)
public class BizReproduceConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long configId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 产品背景配置JSON
     */
    private String productConfigJson;

    /**
     * 人物参考图JSON
     */
    private String charImages;

    /**
     * 商品参考图JSON
     */
    private String productImages;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;
}
