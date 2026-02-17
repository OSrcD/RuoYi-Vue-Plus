package org.dromara.business.domain.vo;

import org.dromara.business.domain.BizPromptCommentComplete;
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

    /**
     * 小红书笔记信息
     */
    @ExcelProperty(value = "小红书笔记信息")
    private String xhsNoteInfo;

    /**
     * 检查状态（0未检查 1已检查）
     */
    @ExcelProperty(value = "检查状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=未检查,1=已检查")
    private Integer checkStatus;

    /**
     * 评论状态（0正常 1吞评）
     */
    @ExcelProperty(value = "评论状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=吞评")
    private Integer commentStatus;

    /**
     * 小红书非吞评次数
     */
    @ExcelProperty(value = "小红书非吞评次数")
    private Integer xhsNormalCount;

    /**
     * 小红书吞评次数
     */
    @ExcelProperty(value = "小红书吞评次数")
    private Integer xhsInterceptCount;

    /**
     * 提示词评论内容
     */
    @ExcelProperty(value = "提示词评论内容")
    private String commentContent;

    /**
     * 笔记链接
     */
    private String noteUrl;

    /**
     * 账号名称
     */
    private String accountName;
}
