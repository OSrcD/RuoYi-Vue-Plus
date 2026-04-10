package org.dromara.business.service;

import org.dromara.business.domain.BizScraperPost;

/**
 * 采集详情业务 Service 接口
 */
public interface IBizScraperPostService {
    
    /**
     * 保存采集数据
     * @param post 业务抓取对象
     * @return true=保存成功，false=已存在(去重)
     */
    boolean saveScrapedData(BizScraperPost post);

    /**
     * 获取已存在的外部帖子 ID 集合
     * @param platform 平台标识
     * @param postIds 待校验的原始 ID 列表
     * @return 数据库中已存在的 ID 列表
     */
    java.util.List<String> selectExistingIds(String platform, java.util.List<String> postIds);

    /**
     * 标记帖子视频链接为"待刷新"
     * @param scraperId 帖子主键ID
     */
    void markVideoRefresh(Long scraperId);

    /**
     * 获取所有待刷新视频链接的帖子
     * @return 待刷新帖子列表
     */
    java.util.List<BizScraperPost> getPendingRefreshPosts();

    /**
     * 更新帖子的媒体链接数据 (图片+视频)
     * @param scraperId 帖子主键ID
     * @param images 新的图片JSON
     * @param videos 新的视频JSON
     */
    void updateMedia(Long scraperId, String images, String videos);
    /**
     * 分页查询采集结果
     * @param platform 平台(可选)
     * @param keyword 关键词(可选)
     * @return 分页数据
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizScraperPost> selectPageList(Integer pageNum, Integer pageSize, String platform, String keyword);

    /**
     * 更新素材复刻信息
     */
    void updateRestyleInfo(Long scraperId, String restyleInfo);
}
