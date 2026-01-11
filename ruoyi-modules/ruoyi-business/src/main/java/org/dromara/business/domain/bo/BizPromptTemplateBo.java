package org.dromara.business.domain.bo;

import org.dromara.business.domain.BizPromptTemplate;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 提示词模板业务对象 biz_prompt_template
 *
 * @author Lion Li
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizPromptTemplate.class, reverseConvertGenerate = false)
public class BizPromptTemplateBo extends BaseEntity {

    /**
     * 提示词ID
     */
    @NotNull(message = "提示词ID不能为空", groups = { EditGroup.class })
    private Long promptId;

    /**
     * 提示词模板
     */
    @NotBlank(message = "提示词模板不能为空", groups = { AddGroup.class, EditGroup.class })
    private String template;

    /**
     * 提示词分类（0棋牌 1对象）
     */
    @NotNull(message = "提示词分类（0棋牌 1对象）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long templateType;

    /**
     * 状态（0停用 1启用 2封禁）
     */
    private Long status;

    /**
     * 备注
     */
    private String remark;


}
