package com.ruoyi.business.service;

import com.ruoyi.business.domain.vo.BizPromptTemplateVo;
import com.ruoyi.business.domain.bo.BizPromptTemplateBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 提示词模板Service接口
 *
 * @author Lion Li
 * @date 2026-01-04
 */
public interface IBizPromptTemplateService {

    /**
     * 查询提示词模板
     *
     * @param promptId 主键
     * @return 提示词模板
     */
    BizPromptTemplateVo queryById(Long promptId);

    /**
     * 分页查询提示词模板列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 提示词模板分页列表
     */
    TableDataInfo<BizPromptTemplateVo> queryPageList(BizPromptTemplateBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的提示词模板列表
     *
     * @param bo 查询条件
     * @return 提示词模板列表
     */
    List<BizPromptTemplateVo> queryList(BizPromptTemplateBo bo);

    /**
     * 新增提示词模板
     *
     * @param bo 提示词模板
     * @return 是否新增成功
     */
    Boolean insertByBo(BizPromptTemplateBo bo);

    /**
     * 修改提示词模板
     *
     * @param bo 提示词模板
     * @return 是否修改成功
     */
    Boolean updateByBo(BizPromptTemplateBo bo);

    /**
     * 校验并批量删除提示词模板信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
