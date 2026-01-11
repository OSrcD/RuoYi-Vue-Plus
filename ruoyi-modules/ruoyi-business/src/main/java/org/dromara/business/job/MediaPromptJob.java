package org.dromara.business.job;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.dromara.business.domain.BizPromptTemplate;
import com.ruoyi.business.domain.bo.*;
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

    // 含义：每小时的第 0 分 0 秒执行
    // @Scheduled(cron = "0 0 * * * *")
    // 每秒钟执行一次
    @Scheduled(cron = "* * * * * *")
    public void executeTaskByCron() {
        List<BizPromptTemplate> bizPromptTemplates = bizPromptTemplateMapper.selectList();
        for (BizPromptTemplate bizPromptTemplate : bizPromptTemplates) {
            insertPromptText(bizPromptTemplate);
        }
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
