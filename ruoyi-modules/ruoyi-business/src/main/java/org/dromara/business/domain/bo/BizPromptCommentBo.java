package org.dromara.business.domain.bo;

import org.dromara.business.domain.BizPromptComment;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 提示词评论业务对象 biz_prompt_comment
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizPromptComment.class, reverseConvertGenerate = false)
public class BizPromptCommentBo extends BaseEntity {

    /**
     * 提示词评论编号
     */
    @NotNull(message = "提示词评论编号不能为空", groups = { EditGroup.class })
    private Long commentId;

    /**
     * 提示词模板编号
     */
    @NotNull(message = "提示词模板编号不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "提示词评论内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String commentContent;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户编号
     */
    private String tenantId;

}
