package org.dromara.business.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Gemini视频分析Service接口
 */
public interface IBizGeminiVideoService {

    /**
     * 分析视频内容
     *
     * @param file   视频文件
     * @param prompt 提示词
     * @param mode   模型模式 (fast, thinking, pro)
     * @return 分析结果
     */
    String analyzeVideo(MultipartFile file, String prompt, String mode);

    /**
     * 根据Veo3模板依次串行生成分析结果并获取JSON
     * 
     * @param videoUrl          原始对标带货视频URL
     * @param productConfigJson 产品配置(JSON格式)
     * @param charImageUrls     人物参考图
     * @param productImageUrls  商品参考图
     * @param mode              模型模式
     * @return 返回最终JSON格式字符串
     */
    String generateVeo3Json(String videoUrl, String productConfigJson, java.util.List<String> charImageUrls,
            java.util.List<String> productImageUrls, String mode);

    /**
     * 对截帧图进行AI洗图分析
     * 
     * @param originalImageUrl 原始截帧图URL
     * @param charImageUrls    人物参考图URL列表
     * @param productImageUrls 商品参考图URL列表
     * @return 洗图分析结果JSON（包含 final_prompt 等字段）
     */
    String polishImage(String originalImageUrl, java.util.List<String> charImageUrls,
            java.util.List<String> productImageUrls);

    /**
     * 使用 Gemini 图片生成模型（Nano Banana）生成洗后的图片。
     * 开启一个全新的AI对话，上传原图 + final_prompt，返回生成的图片字节。
     * 由于 Spring AI ChatModel 不支持 responseModalities: IMAGE，此方法直接调用 Gemini REST API。
     *
     * @param originalImageUrl 原始截帧图URL
     * @param finalPrompt      洗图分析阶段生成的最终提示词
     * @return 生成图片的字节数组（PNG格式）
     */
    byte[] generateImage(String originalImageUrl, String finalPrompt);

    /**
     * 使用 Veo 3.1 API 实现图生视频。
     * 将洗图后的图片作为起始帧，结合 i2v 提示词生成 8 秒视频。
     * 该方法为同步阻塞调用（内部轮询等待视频生成完成）。
     *
     * @param imageUrl 起始帧图片URL（通常为洗图后的 polishedImageUrl）
     * @param prompt   图生视频提示词（i2vPromptEn）
     * @param referenceImageUrls 参考图片URL列表（最多3张，用于保持人物/商品一致性）
     * @return 生成视频的字节数组（MP4格式）
     */
    byte[] generateVideoFromImage(String imageUrl, String prompt, java.util.List<String> referenceImageUrls);
}
