package org.dromara.business.domain.vo;

import org.dromara.business.domain.BizPromptTemplate;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 提示词模板视图对象 biz_prompt_template
 *
 * @author Lion Li
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizPromptTemplate.class)
public class BizPromptTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示词ID
     */
    @ExcelProperty(value = "提示词ID")
    private Long promptId;

    /**
     * 提示词模板
     */
    @ExcelProperty(value = "提示词模板")
    private String template;

    /**
     * 提示词分类（0棋牌 1对象）
     */
    @ExcelProperty(value = "提示词分类", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "biz_prompt_template_type")
    private Long templateType;

    /**
     * 状态（0停用 1启用 2封禁）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private Long status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;


}
