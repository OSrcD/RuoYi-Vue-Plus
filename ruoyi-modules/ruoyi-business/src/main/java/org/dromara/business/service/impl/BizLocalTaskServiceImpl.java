package org.dromara.business.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.domain.BizLocalTask;
import org.dromara.business.domain.BizVideoReproduceFrame;
import org.dromara.business.domain.BizVideoReproduceTask;
import org.dromara.business.mapper.BizLocalTaskMapper;
import org.dromara.business.mapper.BizVideoReproduceFrameMapper;
import org.dromara.business.mapper.BizVideoReproduceTaskMapper;
import org.dromara.business.service.IBizLocalTaskService;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BizLocalTaskServiceImpl implements IBizLocalTaskService {

    private final BizLocalTaskMapper localTaskMapper;
    private final BizVideoReproduceFrameMapper frameMapper;
    private final BizVideoReproduceTaskMapper taskMapper;

    @Override
    public void enqueueTask(String taskType, Long refTaskId, Long refFrameId, String execParams) {
        BizLocalTask task = new BizLocalTask();
        task.setTaskType(taskType);
        task.setRefTaskId(refTaskId);
        task.setRefFrameId(refFrameId);
        task.setExecParams(execParams);
        task.setStatus(0); // 待分配
        localTaskMapper.insert(task);
        log.info("本地任务入队成功, 类型: {}, FrameId: {}", taskType, refFrameId);
    }

    @Override
    public BizLocalTask pollTask() {
        // 先查最早的一条待分配状态记录
        LambdaQueryWrapper<BizLocalTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLocalTask::getStatus, 0)
                .orderByAsc(BizLocalTask::getCreateTime)
                .last("LIMIT 1");

        BizLocalTask task = localTaskMapper.selectOne(wrapper);
        if (task != null) {
            // 设置状态为运行中
            task.setStatus(1);
            localTaskMapper.updateById(task);
            return task;
        }
        return null;
    }

    @Override
    public List<BizLocalTask> list() {
        return localTaskMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public long getCount() {
        LambdaQueryWrapper<BizLocalTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLocalTask::getStatus, 0);
        return localTaskMapper.selectCount(wrapper);
    }

    @Override
    public void completeTask(Long taskId, boolean success, String resultData, String errorMsg) {
        BizLocalTask localTask = localTaskMapper.selectById(taskId);
        if (localTask == null) {
            throw new ServiceException("本地回调任务ID不存在: " + taskId);
        }

        localTask.setStatus(success ? 2 : 3);
        localTask.setResultData(resultData);
        localTask.setErrorMsg(errorMsg);
        localTaskMapper.updateById(localTask);

        // 如果成功，继续更新相关领域的记录
        if (success) {
            Long frameId = localTask.getRefFrameId();
            if (frameId != null) {
                BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
                if (frame != null) {
                    try {
                        JSONObject resultJson = JSONUtil.parseObj(resultData);
                        if ("WASH_IMAGE".equals(localTask.getTaskType())) {
                            String polishedUrl = resultJson.getStr("url");
                            if (StrUtil.isNotBlank(polishedUrl)) {
                                if (frame.getPolishedImageUrl() != null && !frame.getPolishedImageUrl().equals(polishedUrl)) {
                                    frame.setPrevPolishedUrl(frame.getPolishedImageUrl());
                                }
                                frame.setPolishedImageUrl(polishedUrl);
                                frame.setStatus("2"); // 洗图完成
                                frameMapper.updateById(frame);
                            }
                        } else if ("GEN_VIDEO".equals(localTask.getTaskType())) {
                            String videoUrl = resultJson.getStr("url");
                            if (StrUtil.isNotBlank(videoUrl)) {
                                if (frame.getGeneratedVideoUrl() != null && !frame.getGeneratedVideoUrl().equals(videoUrl)) {
                                    frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
                                }
                                frame.setGeneratedVideoUrl(videoUrl);
                                frame.setStatus("3"); // 视频生完
                                frameMapper.updateById(frame);
                            }
                        }
                    } catch (Exception e) {
                        log.error("解析本地任务结果异常", e);
                    }
                }
            } else if ("ANALYZE_VIDEO".equals(localTask.getTaskType())) {
                try {
                    // resultData 应该是包含三个分析阶段最终 JSON 的字符串
                    JSONObject resultJson = JSONUtil.parseObj(resultData);
                    
                    // 获取 IBizVideoReproduceService (动态获取避免循环依赖)
                    org.dromara.business.service.IBizVideoReproduceService videoReproduceService = 
                        org.dromara.common.core.utils.SpringUtils.getBean(org.dromara.business.service.IBizVideoReproduceService.class);
                    
                    // 继续执行后半段流程，拆解全局锁和切分组装截帧
                    videoReproduceService.continueFullWorkflowAfterAnalysis(localTask.getRefTaskId(), resultData);
                } catch (Exception e) {
                    log.error("处理 ANALYZE_VIDEO 回调异常", e);
                    BizVideoReproduceTask taskUpdate = new BizVideoReproduceTask();
                    taskUpdate.setTaskId(localTask.getRefTaskId());
                    taskUpdate.setStatus("9");
                    taskUpdate.setErrorMsg("处理本地回传的分析结果失败: " + e.getMessage());
                    taskMapper.updateById(taskUpdate);
                }
            } else if ("BATCH_GEN".equals(localTask.getTaskType())) {
                // 如果是按任务批量生成的全量宏观回调处理
            }
        }
    }
}
