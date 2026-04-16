package org.dromara.business.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 视频复刻任务业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizVideoReproduceTaskBo extends BaseEntity {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 原始对标视频URL
     */
    private String originalVideoUrl;

    /**
     * 状态（0待开始 1分析中 2截帧中 3洗图中 4完成 9失败）
     */
    private String status;

    /**
     * 租户编号
     */
    private String tenantId;
}
