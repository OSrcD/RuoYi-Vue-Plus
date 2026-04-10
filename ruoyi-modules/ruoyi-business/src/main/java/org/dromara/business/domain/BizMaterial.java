package org.dromara.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 素材库业务对象 biz_material
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "biz_material")
public class BizMaterial extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 素材ID
     */
    @TableId(value = "material_id", type = IdType.AUTO)
    private Long materialId;

    /**
     * 素材名称
     */
    private String materialName;

    /**
     * 素材地址
     */
    private String materialUrl;

    /**
     * 文件类型（0图片 1视频）
     */
    private String fileType;

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
