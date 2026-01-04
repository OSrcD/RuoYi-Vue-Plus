package com.thek.business.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.thek.business.domain.bo.BizPromptCommentBo;
import com.thek.business.domain.vo.BizPromptCommentVo;
import com.thek.business.domain.BizPromptComment;
import com.thek.business.mapper.BizPromptCommentMapper;
import com.thek.business.service.IBizPromptCommentService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 提示词评论Service业务层处理
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizPromptCommentServiceImpl implements IBizPromptCommentService {

    private final BizPromptCommentMapper baseMapper;

    /**
     * 查询提示词评论
     *
     * @param commentId 主键
     * @return 提示词评论
     */
    @Override
    public BizPromptCommentVo queryById(Long commentId){
        return baseMapper.selectVoById(commentId);
    }

    /**
     * 分页查询提示词评论列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 提示词评论分页列表
     */
    @Override
    public TableDataInfo<BizPromptCommentVo> queryPageList(BizPromptCommentBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizPromptComment> lqw = buildQueryWrapper(bo);
        Page<BizPromptCommentVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的提示词评论列表
     *
     * @param bo 查询条件
     * @return 提示词评论列表
     */
    @Override
    public List<BizPromptCommentVo> queryList(BizPromptCommentBo bo) {
        LambdaQueryWrapper<BizPromptComment> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizPromptComment> buildQueryWrapper(BizPromptCommentBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizPromptComment> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizPromptComment::getCommentId);
        lqw.eq(bo.getPromptId() != null, BizPromptComment::getPromptId, bo.getPromptId());
        lqw.eq(StringUtils.isNotBlank(bo.getCommentContent()), BizPromptComment::getCommentContent, bo.getCommentContent());
        return lqw;
    }

    /**
     * 新增提示词评论
     *
     * @param bo 提示词评论
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizPromptCommentBo bo) {
        BizPromptComment add = MapstructUtils.convert(bo, BizPromptComment.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCommentId(add.getCommentId());
        }
        return flag;
    }

    /**
     * 修改提示词评论
     *
     * @param bo 提示词评论
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizPromptCommentBo bo) {
        BizPromptComment update = MapstructUtils.convert(bo, BizPromptComment.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizPromptComment entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除提示词评论信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
