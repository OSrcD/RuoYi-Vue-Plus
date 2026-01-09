package com.ruoyi.business.domain.vo;

import com.ruoyi.business.domain.BizPromptComment;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 提示词评论视图对象 biz_prompt_comment
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizPromptComment.class)
public class BizPromptCommentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示词评论编号
     */
    @ExcelProperty(value = "提示词评论编号")
    private Long commentId;

    /**
     * 提示词模板编号
     */
    @ExcelProperty(value = "提示词模板编号")
    private Long promptId;

    /**
     * 操作分组ID
     */
    @ExcelProperty(value = "操作分组ID")
    private Long operateGroupId;


    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 提示词评论内容
     */
    @ExcelProperty(value = "提示词评论内容")
    private String commentContent;

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
