package com.ruoyi.business.service;

import com.ruoyi.business.domain.vo.BizPromptCommentCompleteVo;
import com.ruoyi.business.domain.bo.BizPromptCommentCompleteBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 已评论Service接口
 *
 * @author Lion Li
 * @date 2026-01-04
 */
public interface IBizPromptCommentCompleteService {

    /**
     * 查询已评论
     *
     * @param commentCompleteId 主键
     * @return 已评论
     */
    BizPromptCommentCompleteVo queryById(Long commentCompleteId);

    /**
     * 分页查询已评论列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 已评论分页列表
     */
    TableDataInfo<BizPromptCommentCompleteVo> queryPageList(BizPromptCommentCompleteBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的已评论列表
     *
     * @param bo 查询条件
     * @return 已评论列表
     */
    List<BizPromptCommentCompleteVo> queryList(BizPromptCommentCompleteBo bo);

    /**
     * 新增已评论
     *
     * @param bo 已评论
     * @return 是否新增成功
     */
    Boolean insertByBo(BizPromptCommentCompleteBo bo);

    /**
     * 修改已评论
     *
     * @param bo 已评论
     * @return 是否修改成功
     */
    Boolean updateByBo(BizPromptCommentCompleteBo bo);

    /**
     * 校验并批量删除已评论信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
