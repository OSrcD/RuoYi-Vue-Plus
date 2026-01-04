package com.thek.business.domain.vo;

import com.thek.business.domain.BizMediaAccount;
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
 * 自媒体账号视图对象 biz_media_account
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizMediaAccount.class)
public class BizMediaAccountVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自媒体账号编号
     */
    @ExcelProperty(value = "自媒体账号编号")
    private Long id;

    /**
     * 账号ID
     */
    @ExcelProperty(value = "账号ID")
    private String accountId;

    /**
     * 账号名称
     */
    @ExcelProperty(value = "账号名称")
    private String accountName;

    /**
     * 平台
     */
    @ExcelProperty(value = "平台", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "biz_account_platform")
    private Long accountPlatform;

    /**
     * 账号类型
     */
    @ExcelProperty(value = "账号类型")
    private Long accountType;

    /**
     * 账号主页链接
     */
    @ExcelProperty(value = "账号主页链接")
    private String accountUrl;

    /**
     * 粉丝数
     */
    @ExcelProperty(value = "粉丝数")
    private Long followerCount;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private Long status;

    /**
     * 描述
     */
    @ExcelProperty(value = "描述")
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
