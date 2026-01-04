package com.ruoyi.business.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 提示词模板对象 biz_prompt_template
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_prompt_template")
public class BizPromptTemplate extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示词编号
     */
    @TableId(value = "prompt_id")
    private Long promptId;

    /**
     * 提示词模板
     */
    private String template;

    /**
     * 提示词分类
     */
    private Long templateType;

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
