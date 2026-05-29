package org.dromara.business.controller;

import jakarta.servlet.http.HttpServletResponse;
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
    public R<BizVideoReproduceTaskVo> create(@RequestPart("video") MultipartFile video,
                          @RequestParam(value = "productConfigJson", required = false) String productConfigJson,
                          @RequestPart(value = "charImages", required = false) MultipartFile[] charImages,
                          @RequestPart(value = "productImages", required = false) MultipartFile[] productImages,
                          @RequestParam(value = "execMode", defaultValue = "api") String execMode) {
        return R.ok(videoReproduceService.createAndStartTask(video, productConfigJson, charImages, productImages, execMode));
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
    public R<Void> generateAll(@PathVariable Long taskId, @RequestParam(value = "execMode", required = false, defaultValue = "api") String execMode) {
        videoReproduceService.generateAllVideos(taskId, execMode);
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
                             @RequestParam(value = "execMode", required = false, defaultValue = "api") String execMode,
                             @RequestBody(required = false) List<String> refImages) {
        videoReproduceService.washImage(frameId, washMode, customPrompt, refImages, execMode);
        return R.ok();
    }

    /**
     * 一键全部洗图
     */
    @PostMapping("/washAllImages/{taskId}")
    public R<Void> washAllImages(@PathVariable Long taskId,
                                 @RequestParam(value = "washMode", required = false, defaultValue = "original") String washMode,
                                 @RequestParam(value = "customPrompt", required = false) String customPrompt,
                                 @RequestParam(value = "execMode", required = false, defaultValue = "api") String execMode,
                                 @RequestBody(required = false) List<String> refImages) {
        videoReproduceService.washAllImages(taskId, washMode, customPrompt, refImages, execMode);
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
    public R<Void> generateVideo(@PathVariable Long frameId, @RequestParam(value = "execMode", required = false, defaultValue = "api") String execMode) {
        videoReproduceService.generateVideo(frameId, execMode);
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

    /**
     * 合成全片视频
     */
    @PostMapping("/mergeVideos/{taskId}")
    public R<Void> mergeVideos(@PathVariable Long taskId, @RequestBody(required = false) MergeVideoRequest request) {
        List<Long> frameIds = request != null ? request.getFrameIds() : null;
        videoReproduceService.mergeVideos(taskId, frameIds);
        return R.ok();
    }

    public static class MergeVideoRequest {
        private List<Long> frameIds;
        public List<Long> getFrameIds() { return frameIds; }
        public void setFrameIds(List<Long> frameIds) { this.frameIds = frameIds; }
    }

    /**
     * 为单帧绑定音频
     */
    @PostMapping("/bindAudio/{frameId}")
    public R<Void> bindAudio(@PathVariable Long frameId, @RequestPart("audio") MultipartFile audio) {
        videoReproduceService.bindAudio(frameId, audio);
        return R.ok();
    }

    /**
     * 自动裁剪音频（移除前后静音）
     */
    @PostMapping("/autoTrimAudio/{frameId}")
    public R<Void> autoTrimAudio(@PathVariable Long frameId) {
        videoReproduceService.autoTrimAudio(frameId);
        return R.ok();
    }

    /**
     * 手动裁剪音频
     */
    @PostMapping("/manualTrimAudio/{frameId}")
    public R<Void> manualTrimAudio(@PathVariable Long frameId, @RequestParam Double start, @RequestParam Double end) {
        videoReproduceService.manualTrimAudio(frameId, start, end);
        return R.ok();
    }

    /**
     * 将音频同步到视频（音画对齐）
     */
    @PostMapping("/syncAudioToVideo/{frameId}")
    public R<Void> syncAudioToVideo(@PathVariable Long frameId) {
        videoReproduceService.syncAudioToVideo(frameId);
        return R.ok();
    }

    /**
     * 更新单帧提示词
     */
    @PostMapping("/updatePrompts/{frameId}")
    public R<Void> updatePrompts(@PathVariable Long frameId, @RequestBody java.util.Map<String, String> data) {
        videoReproduceService.updateFramePrompts(frameId, data.get("promptEn"), data.get("promptZh"));
        return R.ok();
    }

    /**
     * 删除制作单元
     */
    @DeleteMapping("/frame/{frameId}")
    public R<Void> deleteFrame(@PathVariable Long frameId) {
        videoReproduceService.deleteFrame(frameId);
        return R.ok();
    }

    /**
     * 删除生成的视频
     */
    @DeleteMapping("/frame/video/{frameId}")
    public R<Void> deleteVideo(@PathVariable Long frameId) {
        videoReproduceService.deleteGeneratedVideo(frameId);
        return R.ok();
    }

    /**
     * 删除 AI 洗图
     */
    @DeleteMapping("/frame/wash/{frameId}")
    public R<Void> deleteWash(@PathVariable Long frameId) {
        videoReproduceService.deletePolishedImage(frameId);
        return R.ok();
    }

    /**
     * 删除原始图片
     */
    @DeleteMapping("/frame/image/{frameId}")
    public R<Void> deleteOriginalImage(@PathVariable Long frameId) {
        videoReproduceService.deleteOriginalImage(frameId);
        return R.ok();
    }

    /**
     * 手动重新截取关键帧
     */
    @PostMapping("/recaptureFrame/{frameId}")
    public R<Void> recaptureFrame(@PathVariable Long frameId, @RequestParam Double timestamp) {
        videoReproduceService.recaptureFrame(frameId, timestamp);
        return R.ok();
    }

    /**
     * 手动上传生成的视频（替换 Veo 结果）
     */
    @PostMapping("/uploadGeneratedVideo/{frameId}")
    public R<Void> uploadGeneratedVideo(@PathVariable Long frameId, @RequestPart("video") MultipartFile video) {
        videoReproduceService.uploadGeneratedVideo(frameId, video);
        return R.ok();
    }

    /**
     * 下载生成的视频中的音频
     */
    @GetMapping("/downloadAudio/{frameId}")
    public void downloadAudio(@PathVariable Long frameId, HttpServletResponse response) {
        videoReproduceService.downloadAudio(frameId, response);
    }

    /**
     * 手动上传原始对标图片（替换原有的提取帧）
     */
    @PostMapping("/uploadOriginalImage/{frameId}")
    public R<Void> uploadOriginalImage(@PathVariable Long frameId, @RequestPart("image") MultipartFile image) {
        videoReproduceService.uploadOriginalImage(frameId, image);
        return R.ok();
    }
}
