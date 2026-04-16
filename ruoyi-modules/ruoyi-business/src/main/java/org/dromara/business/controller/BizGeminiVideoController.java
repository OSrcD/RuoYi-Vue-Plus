package org.dromara.business.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.business.service.IBizGeminiVideoService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Gemini视频分析
 */
@Tag(name = "Gemini视频分析", description = "集成 Google Gemini 进行视频内容分析")
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/gemini")
public class BizGeminiVideoController extends BaseController {

    private final IBizGeminiVideoService geminiVideoService;

    /**
     * 上传视频并分析 (免登录测试版)
     */
    @Operation(summary = "视频内容分析", description = "上传视频文件，支持三种模式：fast (快速), thinking (深度思考), pro (高级专业)")
    @SaIgnore
    @PostMapping(value = "/analyze", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> analyzeVideo(
        @io.swagger.v3.oas.annotations.Parameter(description = "视频文件") 
        @RequestPart("file") MultipartFile file, 
        @RequestParam(value = "prompt", defaultValue = "请详细描述这段视频的内容。") String prompt,
        @RequestParam(value = "mode", defaultValue = "fast") String mode) {
        return R.ok(geminiVideoService.analyzeVideo(file, prompt, mode));
    }
//    /**
//     * 上传视频并使用Veo3三步提示词生成结构化JSON
//     */
//    @Operation(summary = "Veo3视频连环提示词生成(JSON)", description = "依次提交三个核心提示词，输出最终结构化JSON")
//    @SaIgnore
//    @PostMapping(value = "/analyzeV3", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
//    public R<String> analyzeVideoV3(
//        @io.swagger.v3.oas.annotations.Parameter(description = "视频文件") 
//        @RequestPart("file") MultipartFile file, 
//        @RequestParam(value = "productConfig", required = false) String productConfig,
//        @RequestPart(value = "charImages", required = false) MultipartFile[] charImages,
//        @RequestPart(value = "productImages", required = false) MultipartFile[] productImages,
//        @RequestParam(value = "mode", defaultValue = "pro") String mode) {
//        return R.ok(geminiVideoService.generateVeo3Json(file, productConfig, charImages, productImages, mode));
//    }
}
