package org.dromara.business.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.business.domain.BizScraperPost;
import org.dromara.business.service.IBizScraperPostService;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaIgnore;
import java.util.List;
import java.util.Collections;
import java.util.Map;

/**
 * 自动化素材采集 业务控制器
 */
@SaIgnore
@RestController
@RequestMapping("/business/scraper")
@RequiredArgsConstructor
public class BizScraperPostController {

    private final IBizScraperPostService scraperPostService;

    /**
     * 保存素材采集业务数据
     */
    @PostMapping("/save")
    public R<Void> save(@RequestBody BizScraperPost post) {
        if (post == null || post.getPlatform() == null || post.getPostId() == null) {
            return R.fail("缺少平台标识或帖子唯一标识");
        }
        boolean res = scraperPostService.saveScrapedData(post);
        return res ? R.ok("插入成功") : R.ok("已跳过重复数据");
    }

    /**
     * 查询素材采集列表
     */
    @SaIgnore
    @GetMapping("/list")
    public R<com.baomidou.mybatisplus.extension.plugins.pagination.Page<BizScraperPost>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String keyword) {
        return R.ok(scraperPostService.selectPageList(pageNum, pageSize, platform, keyword));
    }

    /**
     * 批量检查已存在的帖子 ID
     */
    @PostMapping("/check")
    @SuppressWarnings("unchecked")
    public R<List<String>> check(@RequestBody Map<String, Object> params) {
        String platform = (String) params.get("platform");
        List<String> postIds = (List<String>) params.get("postIds");
        if (postIds == null || postIds.isEmpty()) {
            return R.ok(Collections.emptyList());
        }
        return R.ok(scraperPostService.selectExistingIds(platform, postIds));
    }

    /**
     * 标记帖子视频链接为"待刷新"（后台管理系统调用）
     */
    @PutMapping("/markRefresh/{scraperId}")
    public R<Void> markRefresh(@PathVariable Long scraperId) {
        scraperPostService.markVideoRefresh(scraperId);
        return R.ok("已标记为待刷新");
    }

    /**
     * 获取所有待刷新视频的帖子（Electron 采集器轮询调用）
     */
    @GetMapping("/pendingRefresh")
    public R<List<BizScraperPost>> pendingRefresh() {
        return R.ok(scraperPostService.getPendingRefreshPosts());
    }

    /**
     * 更新帖子的媒体链接（Electron 采集器刷新后回传图片+视频）
     */
    @PutMapping("/updateMedia")
    public R<Void> updateMedia(@RequestBody Map<String, Object> params) {
        Long scraperId = Long.valueOf(params.get("scraperId").toString());
        String images = (String) params.get("images");
        String videos = (String) params.get("videos");
        scraperPostService.updateMedia(scraperId, images, videos);
        return R.ok("媒体链接已更新");
    }

    /**
     * 更新复刻后的素材结果
     */
    @SaIgnore
    @PutMapping("/updateRestyle")
    public R<Void> updateRestyle(@RequestBody Map<String, Object> params) {
        Long scraperId = Long.valueOf(params.get("scraperId").toString());
        String restyleInfo = (String) params.get("restyleInfo");
        scraperPostService.updateRestyleInfo(scraperId, restyleInfo);
        return R.ok("复刻素材已记录");
    }
}
