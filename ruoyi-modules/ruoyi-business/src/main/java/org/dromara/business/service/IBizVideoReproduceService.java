package org.dromara.business.service;

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
    Long createAndStartTask(MultipartFile videoFile, String productConfigJson, MultipartFile[] charImages, MultipartFile[] productImages);

    /**
     * 重试任务
     */
    void retryTask(Long taskId);

    /**
     * 一键生成所有视频
     */
    void generateAllVideos(Long taskId);

    /**
     * 获取任务下的截帧
     */
    List<org.dromara.business.domain.BizVideoReproduceFrame> getFrames(Long taskId);

    /**
     * 单帧洗图
     */
    void washImage(Long frameId, String washMode, String customPrompt, List<String> refImages);

    /**
     * 一键全部洗图
     */
    void washAllImages(Long taskId, String washMode, String customPrompt, List<String> refImages);

    /**
     * 撤回洗图
     */
    void undoWash(Long frameId);

    /**
     * 单帧生成视频
     */
    void generateVideo(Long frameId);

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
}
