package com.thek.business.domain.bo;

import com.thek.business.domain.BizPromptCommentComplete;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 已评论业务对象 biz_prompt_comment_complete
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizPromptCommentComplete.class, reverseConvertGenerate = false)
public class BizPromptCommentCompleteBo extends BaseEntity {

    /**
     * 已评论编号
     */
    @NotNull(message = "已评论编号不能为空", groups = { EditGroup.class })
    private Long commentCompleteId;

    /**
     * 评论编号
     */
    @NotNull(message = "评论编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long commentId;

    /**
     * 自媒体账号编号
     */
    @NotNull(message = "自媒体账号编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long mediaAccountId;

    /**
     * 备注
     */
    private String remark;


}
