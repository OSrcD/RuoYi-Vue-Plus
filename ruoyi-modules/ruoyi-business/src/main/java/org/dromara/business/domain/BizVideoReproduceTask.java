package org.dromara.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 视频复刻任务对象 biz_video_reproduce_task
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_video_reproduce_task")
public class BizVideoReproduceTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "task_id")
    private Long taskId;

    /**
     * 原始对标视频URL
     */
    private String originalVideoUrl;

    /**
     * 产品配置JSON
     */
    private String productConfigJson;

    /**
     * 人物参考图列表
     */
    private String charImages;

    /**
     * 商品参考图列表
     */
    private String productImages;

    /**
     * 阶段3分析结果JSON
     */
    private String resultJson;

    /**
     * 全片统一锁信息
     */
    private String globalLocks;

    /**
     * 状态（0待开始 1分析中 2截帧中 3洗图中 4完成 9失败）
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

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
