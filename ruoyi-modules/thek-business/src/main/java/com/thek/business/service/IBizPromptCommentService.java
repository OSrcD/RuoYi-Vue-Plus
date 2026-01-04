package com.thek.business.service;

import com.thek.business.domain.vo.BizPromptCommentVo;
import com.thek.business.domain.bo.BizPromptCommentBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 提示词评论Service接口
 *
 * @author Lion Li
 * @date 2026-01-04
 */
public interface IBizPromptCommentService {

    /**
     * 查询提示词评论
     *
     * @param commentId 主键
     * @return 提示词评论
     */
    BizPromptCommentVo queryById(Long commentId);

    /**
     * 分页查询提示词评论列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 提示词评论分页列表
     */
    TableDataInfo<BizPromptCommentVo> queryPageList(BizPromptCommentBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的提示词评论列表
     *
     * @param bo 查询条件
     * @return 提示词评论列表
     */
    List<BizPromptCommentVo> queryList(BizPromptCommentBo bo);

    /**
     * 新增提示词评论
     *
     * @param bo 提示词评论
     * @return 是否新增成功
     */
    Boolean insertByBo(BizPromptCommentBo bo);

    /**
     * 修改提示词评论
     *
     * @param bo 提示词评论
     * @return 是否修改成功
     */
    Boolean updateByBo(BizPromptCommentBo bo);

    /**
     * 校验并批量删除提示词评论信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
