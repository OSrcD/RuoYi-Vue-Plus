package org.dromara.business.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.business.domain.bo.BizPromptCommentCompleteBo;
import org.dromara.business.domain.vo.BizPromptCommentCompleteVo;
import org.dromara.business.domain.BizPromptCommentComplete;
import org.dromara.business.mapper.BizPromptCommentCompleteMapper;
import org.dromara.business.service.IBizPromptCommentCompleteService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 已评论Service业务层处理
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizPromptCommentCompleteServiceImpl implements IBizPromptCommentCompleteService {

    private final BizPromptCommentCompleteMapper baseMapper;

    /**
     * 查询已评论
     *
     * @param commentCompleteId 主键
     * @return 已评论
     */
    @Override
    public BizPromptCommentCompleteVo queryById(Long commentCompleteId){
        return baseMapper.selectVoById(commentCompleteId);
    }

    /**
     * 分页查询已评论列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 已评论分页列表
     */
    @Override
    public TableDataInfo<BizPromptCommentCompleteVo> queryPageList(BizPromptCommentCompleteBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizPromptCommentComplete> lqw = buildQueryWrapper(bo);
        Page<BizPromptCommentCompleteVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的已评论列表
     *
     * @param bo 查询条件
     * @return 已评论列表
     */
    @Override
    public List<BizPromptCommentCompleteVo> queryList(BizPromptCommentCompleteBo bo) {
        LambdaQueryWrapper<BizPromptCommentComplete> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizPromptCommentComplete> buildQueryWrapper(BizPromptCommentCompleteBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizPromptCommentComplete> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizPromptCommentComplete::getCommentCompleteId);
        lqw.eq(bo.getCommentId() != null, BizPromptCommentComplete::getCommentId, bo.getCommentId());
        lqw.eq(bo.getMediaAccountId() != null, BizPromptCommentComplete::getMediaAccountId, bo.getMediaAccountId());
        return lqw;
    }

    /**
     * 新增已评论
     *
     * @param bo 已评论
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizPromptCommentCompleteBo bo) {
        BizPromptCommentComplete add = MapstructUtils.convert(bo, BizPromptCommentComplete.class);
        add.setTenantId(bo.getTenantId());
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCommentCompleteId(add.getCommentCompleteId());
        }
        return flag;
    }

    /**
     * 修改已评论
     *
     * @param bo 已评论
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizPromptCommentCompleteBo bo) {
        BizPromptCommentComplete update = MapstructUtils.convert(bo, BizPromptCommentComplete.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizPromptCommentComplete entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除已评论信息
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
