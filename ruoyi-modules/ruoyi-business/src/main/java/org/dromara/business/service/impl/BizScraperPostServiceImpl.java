package org.dromara.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.domain.BizScraperPost;
import org.dromara.business.mapper.BizScraperPostMapper;
import org.dromara.business.service.IBizScraperPostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.dromara.common.tenant.helper.TenantHelper;

/**
 * 采集详情业务 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BizScraperPostServiceImpl implements IBizScraperPostService {

    private final BizScraperPostMapper baseMapper;

    @Override
    public boolean saveScrapedData(BizScraperPost post) {
        try {
            int rows = baseMapper.insert(post);
            if (rows > 0) {
                log.info("【业务采集同步】平台标识:[{}], 帖子ID:[{}] 已存入 biz_scraper_post", post.getPlatform(), post.getPostId());
                return true;
            }
            return false;
        } catch (DuplicateKeyException e) {
            log.info("【业务数据去重】平台标识:[{}], 帖子ID:[{}] 数据已存在，自动跳过", post.getPlatform(), post.getPostId());
            return false;
        } catch (Exception e) {
            log.error("【业务入库异常】平台:[{}], PostID:[{}]", post.getPlatform(), post.getPostId(), e);
            throw e;
        }
    }

    @Override
    public List<String> selectExistingIds(String platform, List<String> postIds) {
        if (postIds == null || postIds.isEmpty()) return new java.util.ArrayList<>();
        
        // 开启忽略租户过滤器，确保全量查重
        return TenantHelper.ignore(() -> 
            baseMapper.selectList(new LambdaQueryWrapper<BizScraperPost>()
                .eq(BizScraperPost::getPlatform, platform)
                .in(BizScraperPost::getPostId, postIds)
                .select(BizScraperPost::getPostId))
                .stream()
                .map(BizScraperPost::getPostId)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void markVideoRefresh(Long scraperId) {
        baseMapper.updateById(new BizScraperPost().setScraperId(scraperId).setVideoStatus("1"));
    }

    @Override
    public List<BizScraperPost> getPendingRefreshPosts() {
        return TenantHelper.ignore(() ->
            baseMapper.selectList(new LambdaQueryWrapper<BizScraperPost>()
                .eq(BizScraperPost::getVideoStatus, "1")
                .select(BizScraperPost::getScraperId, BizScraperPost::getSourceUrl, BizScraperPost::getPlatform))
        );
    }

    @Override
    public void updateMedia(Long scraperId, String images, String videos) {
        baseMapper.updateById(new BizScraperPost()
            .setScraperId(scraperId)
            .setImages(images)
            .setVideos(videos)
            .setVideoStatus("0"));
    }
}
