package com.thek.business.domain.vo;

import com.thek.business.domain.BizPromptTemplate;
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
 * @date 2026-01-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizPromptTemplate.class)
public class BizPromptTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示词编号
     */
    @ExcelProperty(value = "提示词编号")
    private Long promptId;

    /**
     * 提示词模板
     */
    @ExcelProperty(value = "提示词模板")
    private String template;

    /**
     * 提示词分类
     */
    @ExcelProperty(value = "提示词分类", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "biz_prompt_template_type")
    private Long templateType;

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

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;


}
