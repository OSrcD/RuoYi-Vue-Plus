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
import com.thek.business.domain.bo.BizPromptTemplateBo;
import com.thek.business.domain.vo.BizPromptTemplateVo;
import com.thek.business.domain.BizPromptTemplate;
import com.thek.business.mapper.BizPromptTemplateMapper;
import com.thek.business.service.IBizPromptTemplateService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 提示词模板Service业务层处理
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizPromptTemplateServiceImpl implements IBizPromptTemplateService {

    private final BizPromptTemplateMapper baseMapper;

    /**
     * 查询提示词模板
     *
     * @param promptId 主键
     * @return 提示词模板
     */
    @Override
    public BizPromptTemplateVo queryById(Long promptId){
        return baseMapper.selectVoById(promptId);
    }

    /**
     * 分页查询提示词模板列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 提示词模板分页列表
     */
    @Override
    public TableDataInfo<BizPromptTemplateVo> queryPageList(BizPromptTemplateBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizPromptTemplate> lqw = buildQueryWrapper(bo);
        Page<BizPromptTemplateVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的提示词模板列表
     *
     * @param bo 查询条件
     * @return 提示词模板列表
     */
    @Override
    public List<BizPromptTemplateVo> queryList(BizPromptTemplateBo bo) {
        LambdaQueryWrapper<BizPromptTemplate> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizPromptTemplate> buildQueryWrapper(BizPromptTemplateBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizPromptTemplate> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizPromptTemplate::getPromptId);
        lqw.eq(bo.getTemplateType() != null, BizPromptTemplate::getTemplateType, bo.getTemplateType());
        return lqw;
    }

    /**
     * 新增提示词模板
     *
     * @param bo 提示词模板
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizPromptTemplateBo bo) {
        BizPromptTemplate add = MapstructUtils.convert(bo, BizPromptTemplate.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPromptId(add.getPromptId());
        }
        return flag;
    }

    /**
     * 修改提示词模板
     *
     * @param bo 提示词模板
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizPromptTemplateBo bo) {
        BizPromptTemplate update = MapstructUtils.convert(bo, BizPromptTemplate.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizPromptTemplate entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除提示词模板信息
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
