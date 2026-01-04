package com.thek.business.controller;

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
import com.thek.business.domain.vo.BizPromptCommentCompleteVo;
import com.thek.business.domain.bo.BizPromptCommentCompleteBo;
import com.thek.business.service.IBizPromptCommentCompleteService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 已评论
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/promptCommentComplete")
public class BizPromptCommentCompleteController extends BaseController {

    private final IBizPromptCommentCompleteService bizPromptCommentCompleteService;

    /**
     * 查询已评论列表
     */
    @SaCheckPermission("business:promptCommentComplete:list")
    @GetMapping("/list")
    public TableDataInfo<BizPromptCommentCompleteVo> list(BizPromptCommentCompleteBo bo, PageQuery pageQuery) {
        return bizPromptCommentCompleteService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出已评论列表
     */
    @SaCheckPermission("business:promptCommentComplete:export")
    @Log(title = "已评论", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizPromptCommentCompleteBo bo, HttpServletResponse response) {
        List<BizPromptCommentCompleteVo> list = bizPromptCommentCompleteService.queryList(bo);
        ExcelUtil.exportExcel(list, "已评论", BizPromptCommentCompleteVo.class, response);
    }

    /**
     * 获取已评论详细信息
     *
     * @param commentCompleteId 主键
     */
    @SaCheckPermission("business:promptCommentComplete:query")
    @GetMapping("/{commentCompleteId}")
    public R<BizPromptCommentCompleteVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long commentCompleteId) {
        return R.ok(bizPromptCommentCompleteService.queryById(commentCompleteId));
    }

    /**
     * 新增已评论
     */
    @SaCheckPermission("business:promptCommentComplete:add")
    @Log(title = "已评论", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizPromptCommentCompleteBo bo) {
        return toAjax(bizPromptCommentCompleteService.insertByBo(bo));
    }

    /**
     * 修改已评论
     */
    @SaCheckPermission("business:promptCommentComplete:edit")
    @Log(title = "已评论", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizPromptCommentCompleteBo bo) {
        return toAjax(bizPromptCommentCompleteService.updateByBo(bo));
    }

    /**
     * 删除已评论
     *
     * @param commentCompleteIds 主键串
     */
    @SaCheckPermission("business:promptCommentComplete:remove")
    @Log(title = "已评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commentCompleteIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] commentCompleteIds) {
        return toAjax(bizPromptCommentCompleteService.deleteWithValidByIds(List.of(commentCompleteIds), true));
    }
}
