package com.ruoyi.business.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 提示词评论对象 biz_prompt_comment
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_prompt_comment")
public class BizPromptComment extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示词评论编号
     */
    @TableId(value = "comment_id")
    private Long commentId;

    /**
     * 提示词模板编号
     */
    private Long promptId;

    /**
     * 操作分组ID
     */
    private Long operateGroupId;

    /**
     * 标题
     */
    private String title;

    /**
     * 提示词评论内容
     */
    private String commentContent;

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
