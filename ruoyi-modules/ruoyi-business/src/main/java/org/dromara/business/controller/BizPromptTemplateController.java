package org.dromara.business.controller;

import java.util.List;

import cn.dev33.satoken.annotation.SaIgnore;
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
import org.dromara.business.domain.vo.BizPromptTemplateVo;
import org.dromara.business.domain.bo.BizPromptTemplateBo;
import org.dromara.business.service.IBizPromptTemplateService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 提示词模板
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/promptTemplate")
public class BizPromptTemplateController extends BaseController {

    private final IBizPromptTemplateService bizPromptTemplateService;

    /**
     * 查询提示词模板列表
     */
//    @SaCheckPermission("business:promptTemplate:list")
    @SaIgnore
    @GetMapping("/list")
    public TableDataInfo<BizPromptTemplateVo> list(BizPromptTemplateBo bo, PageQuery pageQuery) {
        return bizPromptTemplateService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出提示词模板列表
     */
//    @SaCheckPermission("business:promptTemplate:export")
    @SaIgnore
    @Log(title = "提示词模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizPromptTemplateBo bo, HttpServletResponse response) {
        List<BizPromptTemplateVo> list = bizPromptTemplateService.queryList(bo);
        ExcelUtil.exportExcel(list, "提示词模板", BizPromptTemplateVo.class, response);
    }

    /**
     * 获取提示词模板详细信息
     *
     * @param promptId 主键
     */
//    @SaCheckPermission("business:promptTemplate:query")
    @SaIgnore
    @GetMapping("/{promptId}")
    public R<BizPromptTemplateVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long promptId) {
        return R.ok(bizPromptTemplateService.queryById(promptId));
    }

    /**
     * 新增提示词模板
     */
//    @SaCheckPermission("business:promptTemplate:add")
    @SaIgnore
    @Log(title = "提示词模板", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizPromptTemplateBo bo) {
        return toAjax(bizPromptTemplateService.insertByBo(bo));
    }

    /**
     * 修改提示词模板
     */
//    @SaCheckPermission("business:promptTemplate:edit")
    @SaIgnore
    @Log(title = "提示词模板", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizPromptTemplateBo bo) {
        return toAjax(bizPromptTemplateService.updateByBo(bo));
    }

    /**
     * 删除提示词模板
     *
     * @param promptIds 主键串
     */
//    @SaCheckPermission("business:promptTemplate:remove")
    @SaIgnore
    @Log(title = "提示词模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{promptIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] promptIds) {
        return toAjax(bizPromptTemplateService.deleteWithValidByIds(List.of(promptIds), true));
    }
}
