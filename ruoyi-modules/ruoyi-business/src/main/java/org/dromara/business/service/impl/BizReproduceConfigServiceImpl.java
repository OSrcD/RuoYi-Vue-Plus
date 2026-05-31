package org.dromara.business.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.domain.BizReproduceConfig;
import org.dromara.business.domain.bo.BizReproduceConfigBo;
import org.dromara.business.domain.vo.BizReproduceConfigVo;
import org.dromara.business.mapper.BizReproduceConfigMapper;
import org.dromara.business.service.IBizReproduceConfigService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 视频复刻配置预设Service业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizReproduceConfigServiceImpl implements IBizReproduceConfigService {

    private final BizReproduceConfigMapper baseMapper;

    /**
     * 查询视频复刻配置预设
     */
    @Override
    public BizReproduceConfigVo queryById(Long configId){
        return baseMapper.selectVoById(configId);
    }

    /**
     * 分页查询视频复刻配置预设列表
     */
    @Override
    public TableDataInfo<BizReproduceConfigVo> queryPageList(BizReproduceConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizReproduceConfig> lqw = buildQueryWrapper(bo);
        Page<BizReproduceConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询视频复刻配置预设列表
     */
    @Override
    public List<BizReproduceConfigVo> queryList(BizReproduceConfigBo bo) {
        LambdaQueryWrapper<BizReproduceConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizReproduceConfig> buildQueryWrapper(BizReproduceConfigBo bo) {
        LambdaQueryWrapper<BizReproduceConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(cn.hutool.core.util.StrUtil.isNotBlank(bo.getConfigName()), BizReproduceConfig::getConfigName, bo.getConfigName());
        lqw.orderByDesc(BizReproduceConfig::getCreateTime);
        return lqw;
    }

    /**
     * 新增视频复刻配置预设
     */
    @Override
    public Boolean insertByBo(BizReproduceConfigBo bo) {
        BizReproduceConfig add = MapstructUtils.convert(bo, BizReproduceConfig.class);
        if (add.getConfigId() == null) {
            add.setConfigId(IdUtil.getSnowflakeNextId());
        }
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setConfigId(add.getConfigId());
        }
        return flag;
    }

    /**
     * 修改视频复刻配置预设
     */
    @Override
    public Boolean updateByBo(BizReproduceConfigBo bo) {
        BizReproduceConfig update = MapstructUtils.convert(bo, BizReproduceConfig.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 校验并批量删除视频复刻配置预设信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteByIds(ids) > 0;
    }
}
