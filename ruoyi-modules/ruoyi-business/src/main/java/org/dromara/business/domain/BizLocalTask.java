package org.dromara.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_local_task")
public class BizLocalTask extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String taskType;

    private Long refTaskId;

    private Long refFrameId;

    private String execParams;

    private Integer status;

    private String resultData;

    private String errorMsg;
}
