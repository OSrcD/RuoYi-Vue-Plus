package com.thek.business.domain.bo;

import com.thek.business.domain.BizMediaAccount;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 自媒体账号业务对象 biz_media_account
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizMediaAccount.class, reverseConvertGenerate = false)
public class BizMediaAccountBo extends BaseEntity {

    /**
     * 自媒体账号编号
     */
    @NotNull(message = "自媒体账号编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 账号ID
     */
    @NotBlank(message = "账号ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String accountId;

    /**
     * 账号名称
     */
    private String accountName;

    /**
     * 平台
     */
    private Long accountPlatform;

    /**
     * 账号类型
     */
    private Long accountType;

    /**
     * 账号主页链接
     */
    private String accountUrl;

    /**
     * 粉丝数
     */
    private Long followerCount;

    /**
     * 状态
     */
    private Long status;

    /**
     * 描述
     */
    private String remark;


}
