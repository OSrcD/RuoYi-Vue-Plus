package org.dromara.business.service;

import org.dromara.business.domain.bo.BizReproduceConfigBo;
import org.dromara.business.domain.vo.BizReproduceConfigVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 视频复刻配置预设Service接口
 */
public interface IBizReproduceConfigService {

    /**
     * 查询视频复刻配置预设
     */
    BizReproduceConfigVo queryById(Long configId);

    /**
     * 查询视频复刻配置预设列表
     */
    TableDataInfo<BizReproduceConfigVo> queryPageList(BizReproduceConfigBo bo, PageQuery pageQuery);

    /**
     * 查询视频复刻配置预设列表
     */
    List<BizReproduceConfigVo> queryList(BizReproduceConfigBo bo);

    /**
     * 新增视频复刻配置预设
     */
    Boolean insertByBo(BizReproduceConfigBo bo);

    /**
     * 修改视频复刻配置预设
     */
    Boolean updateByBo(BizReproduceConfigBo bo);

    /**
     * 校验并批量删除视频复刻配置预设信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
