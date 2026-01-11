package org.dromara.business.domain.vo;

import org.dromara.business.domain.BizPromptCommentComplete;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 已评论视图对象 biz_prompt_comment_complete
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizPromptCommentComplete.class)
public class BizPromptCommentCompleteVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 已评论编号
     */
    @ExcelProperty(value = "已评论编号")
    private Long commentCompleteId;

    /**
     * 评论编号
     */
    @ExcelProperty(value = "评论编号")
    private Long commentId;

    /**
     * 自媒体账号编号
     */
    @ExcelProperty(value = "自媒体账号编号")
    private Long mediaAccountId;

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
