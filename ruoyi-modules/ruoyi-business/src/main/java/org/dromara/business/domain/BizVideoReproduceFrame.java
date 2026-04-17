package org.dromara.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 视频复刻截帧及洗图对象 biz_video_reproduce_frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_video_reproduce_frame")
public class BizVideoReproduceFrame extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 截帧ID
     */
    @TableId(value = "frame_id")
    private Long frameId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * JSON中的GU ID
     */
    private String guId;

    /**
     * 时间戳(秒)
     */
    private String timestampSec;

    /**
     * 原截帧URL
     */
    private String originalImageUrl;

    /**
     * 洗图后URL
     */
    private String polishedImageUrl;

    /**
     * 最终图生视频提示词
     */
    private String i2vPromptEn;

    /**
     * 图生视频提示词(中文对照)
     */
    private String i2vPromptZh;

    /**
     * 洗图创作模式 (original/restyled/original_pure/restyled_pure)
     */
    private String washMode;

    /**
     * 用户自定义洗图提示词
     */
    private String washCustomPrompt;

    /**
     * 素材库外部参考图URL列表(JSON)
     */
    private String washRefImages;

    /**
     * 上一次洗图结果(支持撤回)
     */
    private String prevPolishedUrl;

    /**
     * Veo 生成的视频URL
     */
    private String generatedVideoUrl;

    /**
     * 上传的音频URL
     */
    private String audioUrl;

    /**
     * 音频配置信息（如对齐、裁剪点等）
     */
    private String audioConfigJson;

    /**
     * 上一次视频结果(支持撤回)
     */
    private String prevVideoUrl;

    /**
     * 状态（0待开始 1截帧成功 2洗图成功 3视频生成完成）
     */
    private String status;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;

}
