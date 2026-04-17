package org.dromara.business.service;

import org.dromara.business.domain.BizLocalTask;

import java.util.List;
import java.util.Map;

public interface IBizLocalTaskService {

    /**
     * 新增排队任务
     */
    void enqueueTask(String taskType, Long refTaskId, Long refFrameId, String execParams);

    /**
     * 轮询获取一条待执行的任务并锁定
     */
    BizLocalTask pollTask();

    /**
     * 轮询获取一条待执行的任务并锁定
     */
    List<BizLocalTask> list();

    /**
     * 获取待处理任务总数
     */
    long getCount();

    /**
     * 本地客户端反馈执行结果
     */
    void completeTask(Long taskId, boolean success, String resultData, String errorMsg);
}
