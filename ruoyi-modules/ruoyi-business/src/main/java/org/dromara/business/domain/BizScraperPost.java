package org.dromara.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 采集帖子业务对象 biz_scraper_post
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "biz_scraper_post", autoResultMap = true)
public class BizScraperPost extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "scraper_id", type = IdType.ASSIGN_ID)
    private Long scraperId;

    /**
     * 平台标识 (对应字典: 0小红书 1抖音)
     */
    private String platform;

    /**
     * 帖子唯一ID
     */
    private String postId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子正文
     */
    private String content;

    /**
     * 作者
     */
    private String author;

    /**
     * 图片链接JSON数组
     */
    private String images;

    /**
     * 视频链接JSON数组
     */
    private String videos;

    /**
     * 视频链接状态 (0正常 1待刷新 2刷新中)
     */
    private String videoStatus;

    /**
     * 原贴链接
     */
    private String sourceUrl;

    /**
     * 状态 (0正常 1处理中 2已处理)
     */
    private String status;

    /**
     * 备注
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
