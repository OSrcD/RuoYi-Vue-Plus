package org.dromara.business.domain.vo;

import org.dromara.business.domain.BizMediaAccount;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


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
     * 自媒体账号ID
     */
    @ExcelProperty(value = "自媒体账号ID")
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
     * 平台（0小红书 1抖音 2快手 3闲鱼 4视频号 5B站 6其他）
     */
    @ExcelProperty(value = "平台", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "biz_account_platform")
    private Long accountPlatform;

    /**
     * 账号类型（0个人 1企业 2机构 3其他）
     */
    @ExcelProperty(value = "账号类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=个人,1=企业,2=机构,3=其他")
    private Long accountType;

    /**
     * 账号主页链接
     */
    @ExcelProperty(value = "账号主页链接")
    private String accountUrl;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码")
    private String phoneNumber;

    /**
     * 粉丝数
     */
    @ExcelProperty(value = "粉丝数")
    private Long followerCount;

    /**
     * 状态（0停用 1启用 2封禁）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private Long status;

    /**
     * 账号描述
     */
    @ExcelProperty(value = "账号描述")
    private String remark;


}
