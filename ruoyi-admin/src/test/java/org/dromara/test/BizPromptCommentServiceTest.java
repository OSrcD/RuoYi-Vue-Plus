package org.dromara.test;

import org.dromara.business.domain.vo.BizPromptCommentVo;
import org.dromara.business.service.IBizPromptCommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 提示词评论服务测试
 */
@SpringBootTest
@DisplayName("提示词评论服务测试")
public class BizPromptCommentServiceTest {

    @Autowired
    private IBizPromptCommentService iBizPromptCommentService;

    @DisplayName("测试查询未使用的提示词评论列表")
    @Test
    public void testQueryUnusedList() {
        Long mediaAccountId = 2007793238983045121L; // 示例ID
        Long platform = 0L; // 示例平台类型
        List<BizPromptCommentVo> list = iBizPromptCommentService.queryUnusedList(mediaAccountId, platform);
        System.out.println("查询结果条数: " + list.size());
        if (list != null) {
            for (BizPromptCommentVo vo : list) {
                System.out.println("评论ID: " + vo.getCommentId() + ", 内容: " + vo.getCommentContent());
            }
        }
    }

    @DisplayName("测试查询未使用的提示词评论列表")
    @Test
    public void testQueryUnusedListByGroup() {
        Long mediaAccountId = 2007793238983045121L; // 示例ID
        Long platform = 3L; // 示例平台类型
        List<BizPromptCommentVo> list = iBizPromptCommentService.getNextAvailableGroup(mediaAccountId, platform);
        System.out.println("查询结果条数: " + list.size());
        if (list != null) {
            for (BizPromptCommentVo vo : list) {
                System.out.println("操作组ID：" + vo.getOperateGroupId() + "，评论ID: " + vo.getCommentId() + ", 内容: " + vo.getCommentContent());
            }
        }
    }
}
