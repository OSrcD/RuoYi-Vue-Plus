package com.ruoyi.business.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 已评论对象 biz_prompt_comment_complete
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_prompt_comment_complete")
public class BizPromptCommentComplete extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 已评论编号
     */
    @TableId(value = "comment_complete_id")
    private Long commentCompleteId;

    /**
     * 评论编号
     */
    private Long commentId;

    /**
     * 自媒体账号编号
     */
    private Long mediaAccountId;

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
