package org.dromara.business.service;

import org.dromara.business.domain.BizMaterial;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 素材库Service接口
 */
public interface IBizMaterialService {

    /**
     * 查询素材库
     */
    BizMaterial queryById(Long materialId);

    /**
     * 查询素材库列表
     */
    TableDataInfo<BizMaterial> queryPageList(BizMaterial bizMaterial, PageQuery pageQuery);

    /**
     * 查询素材库列表
     */
    List<BizMaterial> queryList(BizMaterial bizMaterial);

    /**
     * 新增素材库
     */
    Boolean insertByBo(BizMaterial bo);

    /**
     * 修改素材库
     */
    Boolean updateByBo(BizMaterial bo);

    /**
     * 校验并批量删除素材库信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
