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
import com.thek.business.domain.bo.BizMediaAccountBo;
import com.thek.business.domain.vo.BizMediaAccountVo;
import com.thek.business.domain.BizMediaAccount;
import com.thek.business.mapper.BizMediaAccountMapper;
import com.thek.business.service.IBizMediaAccountService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 自媒体账号Service业务层处理
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizMediaAccountServiceImpl implements IBizMediaAccountService {

    private final BizMediaAccountMapper baseMapper;

    /**
     * 查询自媒体账号
     *
     * @param id 主键
     * @return 自媒体账号
     */
    @Override
    public BizMediaAccountVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询自媒体账号列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 自媒体账号分页列表
     */
    @Override
    public TableDataInfo<BizMediaAccountVo> queryPageList(BizMediaAccountBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizMediaAccount> lqw = buildQueryWrapper(bo);
        Page<BizMediaAccountVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的自媒体账号列表
     *
     * @param bo 查询条件
     * @return 自媒体账号列表
     */
    @Override
    public List<BizMediaAccountVo> queryList(BizMediaAccountBo bo) {
        LambdaQueryWrapper<BizMediaAccount> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizMediaAccount> buildQueryWrapper(BizMediaAccountBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizMediaAccount> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizMediaAccount::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getAccountId()), BizMediaAccount::getAccountId, bo.getAccountId());
        lqw.like(StringUtils.isNotBlank(bo.getAccountName()), BizMediaAccount::getAccountName, bo.getAccountName());
        lqw.eq(bo.getAccountPlatform() != null, BizMediaAccount::getAccountPlatform, bo.getAccountPlatform());
        lqw.eq(bo.getAccountType() != null, BizMediaAccount::getAccountType, bo.getAccountType());
        lqw.eq(bo.getStatus() != null, BizMediaAccount::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增自媒体账号
     *
     * @param bo 自媒体账号
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizMediaAccountBo bo) {
        BizMediaAccount add = MapstructUtils.convert(bo, BizMediaAccount.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改自媒体账号
     *
     * @param bo 自媒体账号
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizMediaAccountBo bo) {
        BizMediaAccount update = MapstructUtils.convert(bo, BizMediaAccount.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizMediaAccount entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除自媒体账号信息
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
