package org.dromara.business.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.business.domain.BizMaterial;
import org.dromara.business.service.IBizMaterialService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 素材库控制器
 */
@Tag(name = "素材库控制器", description = "素材库管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/material")
public class BizMaterialController extends BaseController {

    private final IBizMaterialService materialService;

    /**
     * 查询素材库列表
     */
    @Operation(summary = "查询素材库列表", description = "查询素材库列表")
    @GetMapping("/list")
    @SaIgnore
    public TableDataInfo<BizMaterial> list(BizMaterial bizMaterial, PageQuery pageQuery) {
        return materialService.queryPageList(bizMaterial, pageQuery);
    }

    /**
     * 获取素材库详细信息
     */
    @Operation(summary = "获取素材库详细信息", description = "获取素材库详细信息")
    @GetMapping(value = "/{materialId}")
    @SaIgnore
    public R<BizMaterial> getInfo(@Parameter(description = "主键", required = true) @PathVariable("materialId") Long materialId) {
        return R.ok(materialService.queryById(materialId));
    }

    /**
     * 新增素材库
     */
    @Operation(summary = "新增素材库", description = "新增素材库")
    @PostMapping()
    @SaIgnore
    public R<Void> add(@RequestBody BizMaterial bizMaterial) {
        return toAjax(materialService.insertByBo(bizMaterial));
    }

    /**
     * 修改素材库
     */
    @Operation(summary = "修改素材库", description = "修改素材库")
    @PutMapping()
    @SaIgnore
    public R<Void> edit(@RequestBody BizMaterial bizMaterial) {
        return toAjax(materialService.updateByBo(bizMaterial));
    }

    /**
     * 删除素材库
     */
    @Operation(summary = "删除素材库", description = "删除素材库")
    @DeleteMapping("/{materialIds}")
    @SaIgnore
    public R<Void> remove(@PathVariable Long[] materialIds) {
        return toAjax(materialService.deleteWithValidByIds(Arrays.asList(materialIds), true));
    }
}
