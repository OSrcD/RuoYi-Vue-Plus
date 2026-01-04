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
import com.thek.business.domain.vo.BizPromptCommentVo;
import com.thek.business.domain.bo.BizPromptCommentBo;
import com.thek.business.service.IBizPromptCommentService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 提示词评论
 *
 * @author Lion Li
 * @date 2026-01-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/promptComment")
public class BizPromptCommentController extends BaseController {

    private final IBizPromptCommentService bizPromptCommentService;

    /**
     * 查询提示词评论列表
     */
    @SaCheckPermission("business:promptComment:list")
    @GetMapping("/list")
    public TableDataInfo<BizPromptCommentVo> list(BizPromptCommentBo bo, PageQuery pageQuery) {
        return bizPromptCommentService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出提示词评论列表
     */
    @SaCheckPermission("business:promptComment:export")
    @Log(title = "提示词评论", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizPromptCommentBo bo, HttpServletResponse response) {
        List<BizPromptCommentVo> list = bizPromptCommentService.queryList(bo);
        ExcelUtil.exportExcel(list, "提示词评论", BizPromptCommentVo.class, response);
    }

    /**
     * 获取提示词评论详细信息
     *
     * @param commentId 主键
     */
    @SaCheckPermission("business:promptComment:query")
    @GetMapping("/{commentId}")
    public R<BizPromptCommentVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long commentId) {
        return R.ok(bizPromptCommentService.queryById(commentId));
    }

    /**
     * 新增提示词评论
     */
    @SaCheckPermission("business:promptComment:add")
    @Log(title = "提示词评论", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizPromptCommentBo bo) {
        return toAjax(bizPromptCommentService.insertByBo(bo));
    }

    /**
     * 修改提示词评论
     */
    @SaCheckPermission("business:promptComment:edit")
    @Log(title = "提示词评论", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizPromptCommentBo bo) {
        return toAjax(bizPromptCommentService.updateByBo(bo));
    }

    /**
     * 删除提示词评论
     *
     * @param commentIds 主键串
     */
    @SaCheckPermission("business:promptComment:remove")
    @Log(title = "提示词评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commentIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] commentIds) {
        return toAjax(bizPromptCommentService.deleteWithValidByIds(List.of(commentIds), true));
    }
}
