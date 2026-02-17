package org.dromara.business.mapper;

import org.dromara.business.domain.BizPromptCommentComplete;
import org.dromara.business.domain.bo.BizPromptCommentCompleteBo;
import org.dromara.business.domain.vo.BizPromptCommentCompleteVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 已评论Mapper接口
 *
 * @author Lion Li
 * @date 2026-01-04
 */
public interface BizPromptCommentCompleteMapper
        extends BaseMapperPlus<BizPromptCommentComplete, BizPromptCommentCompleteVo> {

    Page<BizPromptCommentCompleteVo> selectCheckList(@Param("page") Page<BizPromptCommentCompleteVo> page,
            @Param("bo") BizPromptCommentCompleteBo bo);

}
