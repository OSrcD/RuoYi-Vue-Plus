package org.dromara.business.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.business.domain.BizReproduceConfig;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 视频复刻配置预设业务对象 biz_reproduce_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizReproduceConfig.class, reverseConvertGenerate = false)
public class BizReproduceConfigBo extends BaseEntity {

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空", groups = { EditGroup.class })
    private Long configId;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空", groups = { AddGroup.class, EditGroup.class })
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
}
