package org.dromara.business.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.business.domain.BizVideoReproduceTask;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频复刻任务视图对象
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizVideoReproduceTask.class)
public class BizVideoReproduceTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ExcelProperty(value = "任务ID")
    private Long taskId;

    /**
     * 原始对标视频URL
     */
    @ExcelProperty(value = "原始对标视频URL")
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
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 错误信息
     */
    @ExcelProperty(value = "错误信息")
    private String errorMsg;

    /**
     * 合成后的全片URL
     */
    private String combinedVideoUrl;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

}
