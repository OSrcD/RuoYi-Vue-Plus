package org.dromara.business.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.business.domain.BizLocalTask;
import org.dromara.business.service.IBizLocalTaskService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 自动化本地任务控制器
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/localTask")
public class BizLocalTaskController extends BaseController {

    private final IBizLocalTaskService localTaskService;

    /**
     * 客户端轮询获取待分配任务
     */
    @GetMapping("/poll")
    public R<BizLocalTask> pollTask() {
        BizLocalTask task = localTaskService.pollTask();
        return R.ok(task);
    }

    /**
     * 获取待处理任务总数
     */
    @GetMapping("/count")
    public R<Long> getCount() {
        return R.ok(localTaskService.getCount());
    }

    /**
     * 客户端回调执行结果
     */
    @PostMapping("/callback")
    public R<Void> callback(@RequestBody Map<String, Object> req) {
        Long taskId = Long.valueOf(req.get("taskId").toString());
        boolean success = (Boolean) req.get("success");
        String resultData = req.get("resultData") != null ? req.get("resultData").toString() : null;
        String errorMsg = req.get("errorMsg") != null ? req.get("errorMsg").toString() : null;

        localTaskService.completeTask(taskId, success, resultData, errorMsg);
        return R.ok();
    }
}
