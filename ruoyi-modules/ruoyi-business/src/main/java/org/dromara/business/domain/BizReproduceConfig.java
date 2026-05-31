package org.dromara.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 视频复刻配置预设对象 biz_reproduce_config
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "biz_reproduce_config")
public class BizReproduceConfig extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "config_id")
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
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;
}
