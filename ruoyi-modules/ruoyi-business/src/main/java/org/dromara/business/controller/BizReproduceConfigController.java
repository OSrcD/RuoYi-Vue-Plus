package org.dromara.business.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.business.domain.bo.BizReproduceConfigBo;
import org.dromara.business.domain.vo.BizReproduceConfigVo;
import org.dromara.business.service.IBizReproduceConfigService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 视频复刻配置预设
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/reproduceConfig")
public class BizReproduceConfigController extends BaseController {

    private final IBizReproduceConfigService bizReproduceConfigService;

    /**
     * 查询视频复刻配置预设列表
     */
    @SaIgnore
    @GetMapping("/list")
    public TableDataInfo<BizReproduceConfigVo> list(BizReproduceConfigBo bo, PageQuery pageQuery) {
        return bizReproduceConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取视频复刻配置预设详细信息
     *
     * @param configId 主键
     */
    @SaIgnore
    @GetMapping("/{configId}")
    public R<BizReproduceConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long configId) {
        return R.ok(bizReproduceConfigService.queryById(configId));
    }

    /**
     * 新增视频复刻配置预设
     */
    @SaIgnore
    @Log(title = "视频复刻配置预设", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizReproduceConfigBo bo) {
        return toAjax(bizReproduceConfigService.insertByBo(bo));
    }

    /**
     * 修改视频复刻配置预设
     */
    @SaIgnore
    @Log(title = "视频复刻配置预设", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizReproduceConfigBo bo) {
        return toAjax(bizReproduceConfigService.updateByBo(bo));
    }

    /**
     * 删除视频复刻配置预设
     *
     * @param configIds 主键串
     */
    @SaIgnore
    @Log(title = "视频复刻配置预设", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] configIds) {
        return toAjax(bizReproduceConfigService.deleteWithValidByIds(List.of(configIds), true));
    }
}
