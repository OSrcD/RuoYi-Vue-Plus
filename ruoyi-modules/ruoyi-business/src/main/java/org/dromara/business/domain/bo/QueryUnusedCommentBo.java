package org.dromara.business.domain.bo;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 查询未使用评论请求对象
 */
@Data
public class QueryUnusedCommentBo {

    /**
     * 自媒体账号ID
     */
    @NotNull(message = "自媒体账号ID不能为空")
    private Long mediaAccountId;

    /**
     * 平台（0小红书 1抖音 2快手 3闲鱼 4视频号 5B站 6其他）
     */
    @NotNull(message = "平台不能为空")
    private Long platform;
}
