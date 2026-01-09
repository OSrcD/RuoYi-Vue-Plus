package com.ruoyi.business.mapper;

import com.ruoyi.business.domain.BizPromptComment;
import com.ruoyi.business.domain.vo.BizPromptCommentVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;
import java.util.List;

/**
 * 提示词评论Mapper接口
 *
 * @author Lion Li
 * @date 2026-01-04
 */
public interface BizPromptCommentMapper extends BaseMapperPlus<BizPromptComment, BizPromptCommentVo> {

    /**
     * 批量新增提示词评论（忽略重复）
     *
     * @param list 提示词评论集合
     * @return 影响行数
     */
    int insertIgnoreBatch(@Param("list") Collection<BizPromptComment> list);

    /**
     * 查询未使用的提示词评论列表
     *
     * @param mediaAccountId 自媒体账号ID
     * @param platform       平台
     * @return 提示词评论列表
     */
    List<BizPromptCommentVo> selectUnusedList(@Param("mediaAccountId") Long mediaAccountId, @Param("platform") Long platform);



    /**
     * 获取一组未使用的评论数据
     * @param mediaAccountId 媒体账号ID
     * @param platform 平台类型
     * @return 评论明细列表
     */
    List<BizPromptCommentVo> selectUnusedListByOneGroup(
        @Param("mediaAccountId") Long mediaAccountId,
        @Param("platform") Long platform
    );
}
