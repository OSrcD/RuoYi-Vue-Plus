package org.dromara.business.job;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.dromara.business.domain.BizPromptTemplate;
import org.dromara.business.domain.bo.*;
import org.dromara.business.domain.dto.PrompCommentDto;
import org.dromara.business.mapper.BizPromptTemplateMapper;
import org.dromara.business.service.IBizPromptCommentService;
import org.dromara.business.python.g4f.PollinationsAI;
import org.dromara.business.service.IBizPromptTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MediaPromptJob {

    @Autowired
    private BizPromptTemplateMapper bizPromptTemplateMapper;

    @Autowired
    private IBizPromptCommentService iBizPromptCommentService;

    @Autowired
    private IBizPromptTemplateService bizPromptTemplateService;

    @Autowired
    private org.dromara.business.mapper.BizMediaAccountMapper bizMediaAccountMapper;

    @Autowired
    private org.dromara.business.mapper.BizPromptCommentMapper bizPromptCommentMapper;

    @Autowired
    private org.dromara.business.mapper.BizPromptCommentCompleteMapper bizPromptCommentCompleteMapper;


    // 含义：每小时的第 0 分 0 秒执行
    // @Scheduled(cron = "0 0 * * * *")
    // 每秒钟执行一次
//    @Scheduled(cron = "* * * * * *")
    public void executeTaskByCron() {
//        List<BizPromptTemplate> bizPromptTemplates = bizPromptTemplateMapper.selectList();
//        for (BizPromptTemplate bizPromptTemplate : bizPromptTemplates) {
//            insertPromptText(bizPromptTemplate);
//        }
    }

    // 每分钟查一次，如果这个账号所有的评论列表都用满了，清理biz_prompt_comment_complete下的记录
    @Scheduled(cron = "0 * * * * ?")
    public void clearCompletedCommentsJob() {
        System.out.println("开始执行定期清理已用满评论账号的完成记录任务...");
        log.info("开始执行定期清理已用满评论账号的完成记录任务...");
        List<org.dromara.business.domain.BizMediaAccount> accounts = bizMediaAccountMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        for (org.dromara.business.domain.BizMediaAccount account : accounts) {
            String mediaAccountId = account.getAccountId();
            Long platform = account.getAccountPlatform();
            System.out.println("mediaAccountId:" + mediaAccountId + " platform:"+ platform);
            if (platform == null) {
                continue;
            }
            // 检查是否有未使用列表
            List<org.dromara.business.domain.vo.BizPromptCommentVo> unusedList = bizPromptCommentMapper.selectUnusedList(Long.valueOf(mediaAccountId), platform);
            System.out.println("size:" + unusedList.size());
            if (unusedList == null || unusedList.isEmpty()) {
                // 如果为空，并且complete表里该账号有数据，说明所有有效的评论都已被该账号用完
                Long count = bizPromptCommentCompleteMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<org.dromara.business.domain.BizPromptCommentComplete>()
                    .eq(org.dromara.business.domain.BizPromptCommentComplete::getMediaAccountId, mediaAccountId)
                    .ne(org.dromara.business.domain.BizPromptCommentComplete::getXhsNoteInfo, ""));
                System.out.println("count:" + count);
                if (count != null && count > 0) {
                    System.out.println("自媒体账号ID：" + mediaAccountId +" (平台："+platform+") 评论已全部用完，开始将其 completed 记录(存在xhs_note_info)执行逻辑删除");
                    log.info("自媒体账号ID {} (平台 {}) 评论已全部用完，开始将其 completed 记录(存在xhs_note_info)执行逻辑删除", mediaAccountId, platform);
                    bizPromptCommentCompleteMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<org.dromara.business.domain.BizPromptCommentComplete>()
                        .eq(org.dromara.business.domain.BizPromptCommentComplete::getMediaAccountId, mediaAccountId)
                        .ne(org.dromara.business.domain.BizPromptCommentComplete::getXhsNoteInfo, ""));
                }
            }
        }
        System.out.println("定期清理已用满评论任务执行结束");
        log.info("定期清理已用满评论任务执行结束");
    }

    private void insertPromptText(BizPromptTemplate bizPromptTemplate) {
        if (bizPromptTemplate != null) {
            log.info("当前提示词模板：{} 类型：{} 开始执行，当前时间:{}", bizPromptTemplate.getRemark(), bizPromptTemplate.getTemplateType(), LocalDateTime.now());
//            log.info("提示词模板：{}", bizPromptTemplate);
            if (bizPromptTemplate.getTemplateType() == 2) { // 降低相似度
                String commentByPrompt = PollinationsAI.getCommentByPrompt(bizPromptTemplate.getTemplate());
                BeanOutputConverter<PrompCommentSimBo> converter = new BeanOutputConverter<>(PrompCommentSimBo.class);
                PrompCommentSimBo convert = converter.convert(commentByPrompt);
                log.info("获取到的评论内容：{}", convert);
                if (convert != null && convert.getCommentList() != null) {
                    List<BizPromptCommentBo> boList = new ArrayList<>();
                    List<CommentSimilarityBo> commentList = convert.getCommentList();
                    for (CommentSimilarityBo commentSimilarityBo : commentList) {
                        BizPromptCommentBo bo = new BizPromptCommentBo();
                        bo.setPromptId(bizPromptTemplate.getPromptId());
                        bo.setTitle(commentSimilarityBo.getSearchTile());
                        bo.setCommentContent(commentSimilarityBo.getComments());
                        bo.setTenantId(bizPromptTemplate.getTenantId());
                        boList.add(bo);
                    }
                    Boolean result = iBizPromptCommentService.insertBatch(boList);
                    log.info("插入的情况:{}", result);
                }
            } else if (bizPromptTemplate.getTemplateType() == 3) { // AI操作系列
                String aiOperatePrompt = PollinationsAI.getCommentByPrompt(bizPromptTemplate.getTemplate());
                BeanOutputConverter<AIOperateListBo> converter = new BeanOutputConverter<>(AIOperateListBo.class);
                AIOperateListBo convert = converter.convert(aiOperatePrompt);
                log.info("获取到的评论内容：{}", convert);
                if (convert != null && convert.getOperateList() != null) {
                    List<BizPromptCommentBo> boList = new ArrayList<>();
                    List<AIOperateSequenceBo> operateList = convert.getOperateList();
                    long operateGroupId = IdWorker.getId();
                    for (AIOperateSequenceBo aiOperateSequenceBo : operateList) {
                        BizPromptCommentBo bo = new BizPromptCommentBo();
                        bo.setPromptId(bizPromptTemplate.getPromptId());
                        bo.setCommentContent(aiOperateSequenceBo.getOperate());
                        bo.setOperateGroupId(operateGroupId);
                        bo.setRemark("时间段：" + aiOperateSequenceBo.getTimeOfDay() + "，操作时间：" + aiOperateSequenceBo.getCurrentTime());
                        bo.setTenantId(bizPromptTemplate.getTenantId());
                        boList.add(bo);
                    }
                    Boolean result = iBizPromptCommentService.insertBatch(boList);
                    log.info("插入的情况:{}", result);
                }
            } else {
                String commentByPrompt = PollinationsAI.getCommentByPrompt(bizPromptTemplate.getTemplate());
                BeanOutputConverter<PrompCommentDto> converter = new BeanOutputConverter<>(PrompCommentDto.class);
                PrompCommentDto convert = converter.convert(commentByPrompt);
                log.info("获取到的评论内容：{}", convert);
                if (convert != null && convert.getCommentList() != null) {
                    List<BizPromptCommentBo> boList = new ArrayList<>();
                    for (String comment : convert.getCommentList()) {
                        // 对内容过滤，比如不包含棋牌文字的 80%大模型做 剩下的20%程序处理
                        BizPromptCommentBo bo = new BizPromptCommentBo();
                        bo.setPromptId(bizPromptTemplate.getPromptId());
                        bo.setCommentContent(comment);
                        bo.setTenantId(bizPromptTemplate.getTenantId());
                        boList.add(bo);
                    }
                    Boolean result = iBizPromptCommentService.insertBatch(boList);
                    log.info("插入的情况:{}", result);
                }
            }
        }
        log.info("当前提示词模板：{} 类型：{} 结束执行，当前时间:{}", bizPromptTemplate.getRemark(), bizPromptTemplate.getTemplateType(), LocalDateTime.now());
    }

}
