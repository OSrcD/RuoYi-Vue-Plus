package org.dromara.business.service;

import jakarta.servlet.http.HttpServletResponse;
import org.dromara.business.domain.bo.BizVideoReproduceTaskBo;
import org.dromara.business.domain.vo.BizVideoReproduceTaskVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 视频复刻任务Service接口
 */
public interface IBizVideoReproduceService {

    /**
     * 查询视频复刻任务列表
     */
    TableDataInfo<BizVideoReproduceTaskVo> queryPageList(BizVideoReproduceTaskBo bo, PageQuery pageQuery);

    /**
     * 创建复刻任务
     */
    BizVideoReproduceTaskVo createAndStartTask(MultipartFile videoFile, String productConfigJson, MultipartFile[] charImages, MultipartFile[] productImages, String execMode);

    /**
     * 在拿到分析的JSON后继续完整的后续流程（保存全局锁、截帧）
     */
    void continueFullWorkflowAfterAnalysis(Long taskId, String resultJson);

    void syncAudioToVideo(Long frameId);

    /**
     * 重试任务
     */
    void retryTask(Long taskId);

    /**
     * 一键生成所有视频
     */
    void generateAllVideos(Long taskId, String execMode);

    /**
     * 获取任务下的截帧
     */
    List<org.dromara.business.domain.BizVideoReproduceFrame> getFrames(Long taskId);

    /**
     * 单帧洗图
     */
    void washImage(Long frameId, String washMode, String customPrompt, List<String> refImages, String execMode);

    /**
     * 一键全部洗图
     */
    void washAllImages(Long taskId, String washMode, String customPrompt, List<String> refImages, String execMode);

    /**
     * 撤回洗图
     */
    void undoWash(Long frameId);

    /**
     * 单帧生成视频
     */
    void generateVideo(Long frameId, String execMode);

    /**
     * 撤回视频
     */
    void undoVideo(Long frameId);

    /**
     * 删除任务及下属的所有关联帧
     */
    void deleteTask(Long taskId);

    /**
     * 对生成的视频进行剪辑（去除指定区间并拼接）
     */
    void clipVideo(Long frameId, java.util.List<java.util.Map<String, Double>> removeRanges);
    /**
     * 合成全片视频
     */
    void mergeVideos(Long taskId, List<Long> frameIds);

    /**
     * 为单帧绑定/上传音频
     */
    void bindAudio(Long frameId, MultipartFile audioFile);

    /**
     * 自动裁剪音频（移除前后静音）
     */
    void autoTrimAudio(Long frameId);

    /**
     * 手动裁剪音频
     */
    void manualTrimAudio(Long frameId, Double start, Double end);

    /**
     * 更新单帧提示词
     */
    void updateFramePrompts(Long frameId, String promptEn, String promptZh);

    /**
     * 删除制作单元（整个帧记录）
     */
    void deleteFrame(Long frameId);

    /**
     * 删除生成的视频
     */
    void deleteGeneratedVideo(Long frameId);

    /**
     * 删除 AI 洗图图片
     */
    void deletePolishedImage(Long frameId);

    /**
     * 删除原始截帧图片
     */
    void deleteOriginalImage(Long frameId);

    /**
     * 手动重新截取关键帧
     */
    void recaptureFrame(Long frameId, Double timestamp);

    /**
     * 手动上传生成的视频（替换 Veo 结果）
     */
    void uploadGeneratedVideo(Long frameId, MultipartFile videoFile);

    /**
     * 手动上传原始对标图片（替换原有的源图片）
     */
    void uploadOriginalImage(Long frameId, MultipartFile imageFile);

    /**
     * 下载生成的视频中的音频
     */
    void downloadAudio(Long frameId, HttpServletResponse response);
}
