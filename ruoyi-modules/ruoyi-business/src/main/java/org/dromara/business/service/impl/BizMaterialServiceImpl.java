package org.dromara.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.business.domain.BizMaterial;
import org.dromara.business.mapper.BizMaterialMapper;
import org.dromara.business.service.IBizMaterialService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 素材库Service业务层处理
 */
@RequiredArgsConstructor
@Service
public class BizMaterialServiceImpl implements IBizMaterialService {

    private final BizMaterialMapper baseMapper;

    /**
     * 查询素材库
     */
    @Override
    public BizMaterial queryById(Long materialId){
        return baseMapper.selectById(materialId);
    }

    /**
     * 查询素材库列表
     */
    @Override
    public TableDataInfo<BizMaterial> queryPageList(BizMaterial bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizMaterial> lqw = buildQueryWrapper(bo);
        Page<BizMaterial> result = baseMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询素材库列表
     */
    @Override
    public List<BizMaterial> queryList(BizMaterial bo) {
        LambdaQueryWrapper<BizMaterial> lqw = buildQueryWrapper(bo);
        return baseMapper.selectList(lqw);
    }

    private LambdaQueryWrapper<BizMaterial> buildQueryWrapper(BizMaterial bo) {
        LambdaQueryWrapper<BizMaterial> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getMaterialName()), BizMaterial::getMaterialName, bo.getMaterialName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileType()), BizMaterial::getFileType, bo.getFileType());
        return lqw;
    }

    /**
     * 新增素材库
     */
    @Override
    public Boolean insertByBo(BizMaterial bo) {
        BizMaterial add = BeanUtil.toBean(bo, BizMaterial.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMaterialId(add.getMaterialId());
        }
        return flag;
    }

    /**
     * 修改素材库
     */
    @Override
    public Boolean updateByBo(BizMaterial bo) {
        BizMaterial update = BeanUtil.toBean(bo, BizMaterial.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除素材库
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
