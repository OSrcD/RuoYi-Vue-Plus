package com.ruoyi.business.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.business.domain.BizPromptTemplate;
import com.ruoyi.business.domain.dto.PrompCommentDto;
import com.ruoyi.business.domain.bo.BizPromptCommentBo;
import com.ruoyi.business.mapper.BizPromptTemplateMapper;
import com.ruoyi.business.service.IBizPromptCommentService;
import com.ruoyi.business.python.g4f.PollinationsAI;
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

    // 含义：每小时的第 0 分 0 秒执行
    // @Scheduled(cron = "0 0 * * * *")
    // 每秒钟执行一次
    @Scheduled(cron = "* * * * * *")
    public void executeTaskByCron() {

        log.info("对象提示模板评论任务开始执行，当前时间：{}", LocalDateTime.now());
        insertPromptText(1);
        log.info("对象提示模板评论任务结束执行，当前时间：{}", LocalDateTime.now());

        log.info("棋牌提示模板评论任务开始执行，当前时间：{}", LocalDateTime.now());
        insertPromptText(0);
        log.info("棋牌提示模板评论任务结束执行，当前时间：{}", LocalDateTime.now());


    }

    private void insertPromptText(int templateType) {
        BizPromptTemplate bizPromptTemplate = bizPromptTemplateMapper
                .selectOne(new LambdaQueryWrapper<BizPromptTemplate>().eq(BizPromptTemplate::getTemplateType, templateType));
        if (bizPromptTemplate != null) {
//            log.info("提示词模板：{}", bizPromptTemplate);
            String commentByPrompt = PollinationsAI.getCommentByPrompt(bizPromptTemplate.getTemplate());
            BeanOutputConverter<PrompCommentDto> converter = new BeanOutputConverter<>(PrompCommentDto.class);
            PrompCommentDto convert = converter.convert(commentByPrompt);
            log.info("获取到的评论内容：{}", convert);


            if (convert != null && convert.getCommentList() != null) {
                List<BizPromptCommentBo> boList = new ArrayList<>();
                for (String comment : convert.getCommentList()) {
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

}
