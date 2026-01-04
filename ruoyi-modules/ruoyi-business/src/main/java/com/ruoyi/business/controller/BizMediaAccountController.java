package com.ruoyi.business.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import com.ruoyi.business.domain.vo.BizMediaAccountVo;
import com.ruoyi.business.domain.bo.BizMediaAccountBo;
import com.ruoyi.business.service.IBizMediaAccountService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 自媒体账号
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/mediaAccount")
public class BizMediaAccountController extends BaseController {

    private final IBizMediaAccountService bizMediaAccountService;

    /**
     * 查询自媒体账号列表
     */
    @SaCheckPermission("business:mediaAccount:list")
    @GetMapping("/list")
    public TableDataInfo<BizMediaAccountVo> list(BizMediaAccountBo bo, PageQuery pageQuery) {
        return bizMediaAccountService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出自媒体账号列表
     */
    @SaCheckPermission("business:mediaAccount:export")
    @Log(title = "自媒体账号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizMediaAccountBo bo, HttpServletResponse response) {
        List<BizMediaAccountVo> list = bizMediaAccountService.queryList(bo);
        ExcelUtil.exportExcel(list, "自媒体账号", BizMediaAccountVo.class, response);
    }

    /**
     * 获取自媒体账号详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("business:mediaAccount:query")
    @GetMapping("/{id}")
    public R<BizMediaAccountVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizMediaAccountService.queryById(id));
    }

    /**
     * 新增自媒体账号
     */
    @SaCheckPermission("business:mediaAccount:add")
    @Log(title = "自媒体账号", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizMediaAccountBo bo) {
        return toAjax(bizMediaAccountService.insertByBo(bo));
    }

    /**
     * 修改自媒体账号
     */
    @SaCheckPermission("business:mediaAccount:edit")
    @Log(title = "自媒体账号", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizMediaAccountBo bo) {
        return toAjax(bizMediaAccountService.updateByBo(bo));
    }

    /**
     * 删除自媒体账号
     *
     * @param ids 主键串
     */
    @SaCheckPermission("business:mediaAccount:remove")
    @Log(title = "自媒体账号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizMediaAccountService.deleteWithValidByIds(List.of(ids), true));
    }
}
