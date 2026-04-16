package org.dromara.business.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.business.domain.BizVideoReproduceFrame;
import org.dromara.business.domain.bo.BizVideoReproduceTaskBo;
import org.dromara.business.domain.vo.BizVideoReproduceTaskVo;
import org.dromara.business.service.IBizVideoReproduceService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 视频复刻任务控制器
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/videoReproduce")
public class BizVideoReproduceController extends BaseController {

    private final IBizVideoReproduceService videoReproduceService;

    /**
     * 查询视频复刻任务列表
     */
    @GetMapping("/list")
    public TableDataInfo<BizVideoReproduceTaskVo> list(BizVideoReproduceTaskBo bo, PageQuery pageQuery) {
        return videoReproduceService.queryPageList(bo, pageQuery);
    }

    /**
     * 创建并开始复刻任务
     * @param video 视频文件
     * @param productConfigJson 产品背景配置
     * @param charImages 人物参考图
     * @param productImages 商品参考图
     */
    @PostMapping("/create")
    public R<Long> create(@RequestPart("video") MultipartFile video,
                          @RequestParam(value = "productConfigJson", required = false) String productConfigJson,
                          @RequestPart(value = "charImages", required = false) MultipartFile[] charImages,
                          @RequestPart(value = "productImages", required = false) MultipartFile[] productImages) {
        return R.ok(videoReproduceService.createAndStartTask(video, productConfigJson, charImages, productImages));
    }

    /**
     * 重试任务
     */
    @PostMapping("/retry/{taskId}")
    public R<Void> retry(@PathVariable Long taskId) {
        videoReproduceService.retryTask(taskId);
        return R.ok();
    }

    /**
     * 一键生成视频
     */
    @PostMapping("/generateAll/{taskId}")
    public R<Void> generateAll(@PathVariable Long taskId) {
        videoReproduceService.generateAllVideos(taskId);
        return R.ok();
    }

    /**
     * 查询任务截帧详情
     */
    @GetMapping("/frames/{taskId}")
    public R<List<BizVideoReproduceFrame>> getFrames(@PathVariable Long taskId) {
        return R.ok(videoReproduceService.getFrames(taskId));
    }

    /**
     * 单帧洗图
     */
    @PostMapping("/washImage/{frameId}")
    public R<Void> washImage(@PathVariable Long frameId,
                             @RequestParam(value = "washMode", required = false, defaultValue = "original") String washMode,
                             @RequestParam(value = "customPrompt", required = false) String customPrompt,
                             @RequestBody(required = false) List<String> refImages) {
        videoReproduceService.washImage(frameId, washMode, customPrompt, refImages);
        return R.ok();
    }

    /**
     * 一键全部洗图
     */
    @PostMapping("/washAllImages/{taskId}")
    public R<Void> washAllImages(@PathVariable Long taskId,
                                 @RequestParam(value = "washMode", required = false, defaultValue = "original") String washMode,
                                 @RequestParam(value = "customPrompt", required = false) String customPrompt,
                                 @RequestBody(required = false) List<String> refImages) {
        videoReproduceService.washAllImages(taskId, washMode, customPrompt, refImages);
        return R.ok();
    }

    /**
     * 撤回洗图
     */
    @PostMapping("/undoWash/{frameId}")
    public R<Void> undoWash(@PathVariable Long frameId) {
        videoReproduceService.undoWash(frameId);
        return R.ok();
    }

    /**
     * 单帧生成视频
     */
    @PostMapping("/generateVideo/{frameId}")
    public R<Void> generateVideo(@PathVariable Long frameId) {
        videoReproduceService.generateVideo(frameId);
        return R.ok();
    }

    /**
     * 撤回视频
     */
    @PostMapping("/undoVideo/{frameId}")
    public R<Void> undoVideo(@PathVariable Long frameId) {
        videoReproduceService.undoVideo(frameId);
        return R.ok();
    }

    /**
     * 删除任务及下属的所有拆解帧
     */
    @DeleteMapping("/{taskId}")
    public R<Void> remove(@PathVariable Long taskId) {
        videoReproduceService.deleteTask(taskId);
        return R.ok();
    }

    /**
     * 对生成的视频进行剪辑（去除指定区间并拼接）
     */
    @PostMapping("/clipVideo/{frameId}")
    public R<Void> clipVideo(@PathVariable Long frameId, @RequestBody java.util.List<java.util.Map<String, Double>> removeRanges) {
        videoReproduceService.clipVideo(frameId, removeRanges);
        return R.ok();
    }
}
