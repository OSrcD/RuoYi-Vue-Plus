package com.ruoyi.business.service;

import com.ruoyi.business.domain.vo.BizMediaAccountVo;
import com.ruoyi.business.domain.bo.BizMediaAccountBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 自媒体账号Service接口
 *
 * @author Lion Li
 * @date 2026-01-04
 */
public interface IBizMediaAccountService {

    /**
     * 查询自媒体账号
     *
     * @param id 主键
     * @return 自媒体账号
     */
    BizMediaAccountVo queryById(Long id);

    /**
     * 分页查询自媒体账号列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 自媒体账号分页列表
     */
    TableDataInfo<BizMediaAccountVo> queryPageList(BizMediaAccountBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的自媒体账号列表
     *
     * @param bo 查询条件
     * @return 自媒体账号列表
     */
    List<BizMediaAccountVo> queryList(BizMediaAccountBo bo);

    /**
     * 新增自媒体账号
     *
     * @param bo 自媒体账号
     * @return 是否新增成功
     */
    Boolean insertByBo(BizMediaAccountBo bo);

    /**
     * 修改自媒体账号
     *
     * @param bo 自媒体账号
     * @return 是否修改成功
     */
    Boolean updateByBo(BizMediaAccountBo bo);

    /**
     * 校验并批量删除自媒体账号信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
