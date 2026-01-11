package org.dromara.business.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 自媒体账号对象 biz_media_account
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_media_account")
public class BizMediaAccount extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自媒体账号ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 账号名称
     */
    private String accountName;

    /**
     * 平台（0小红书 1抖音 2快手 3闲鱼 4视频号 5B站 6其他）
     */
    private Long accountPlatform;

    /**
     * 账号类型（0个人 1企业 2机构 3其他）
     */
    private Long accountType;

    /**
     * 账号主页链接
     */
    private String accountUrl;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 粉丝数
     */
    private Long followerCount;

    /**
     * 状态（0停用 1启用 2封禁）
     */
    private Long status;

    /**
     * 账号描述
     */
    private String remark;

    /**
     * 版本
     */
    @Version
    private Long version;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;


}
