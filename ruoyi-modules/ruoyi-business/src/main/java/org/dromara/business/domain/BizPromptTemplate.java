package org.dromara.business.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 提示词模板对象 biz_prompt_template
 *
 * @author Lion Li
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_prompt_template")
public class BizPromptTemplate extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示词ID
     */
    @TableId(value = "prompt_id")
    private Long promptId;

    /**
     * 提示词模板
     */
    private String template;

    /**
     * 提示词分类（0棋牌 1对象）
     */
    private Long templateType;

    /**
     * 状态（0停用 1启用 2封禁）
     */
    private Long status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本
     */
    @Version
    private Long version;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;


}
