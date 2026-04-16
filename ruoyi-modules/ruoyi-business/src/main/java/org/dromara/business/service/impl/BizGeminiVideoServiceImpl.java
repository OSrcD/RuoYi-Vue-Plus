package org.dromara.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.service.IBizGeminiVideoService;
import org.springframework.ai.content.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Gemini视频分析Service业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizGeminiVideoServiceImpl implements IBizGeminiVideoService {

    private final ChatModel chatModel;

    @org.springframework.beans.factory.annotation.Value("${spring.ai.google.genai.chat.options.model-fast:gemini-3.1-flash-lite-preview}")
    private String fastModel;

    @org.springframework.beans.factory.annotation.Value("${spring.ai.google.genai.chat.options.model-thinking:gemini-3.1-pro-preview-customtools}")
    private String thinkingModel;

    @org.springframework.beans.factory.annotation.Value("${spring.ai.google.genai.chat.options.model-pro:gemini-3.1-pro-preview}")
    private String proModel;

    @Override
    public String analyzeVideo(MultipartFile file, String prompt, String mode) {
        try {
            if (file.isEmpty()) {
                return "文件不能为空";
            }
            log.info("开始使用Gemini分析视频: {}, 大小: {}, 模式: {}",
                    file.getOriginalFilename(), file.getSize(), mode);

            Resource videoResource = file.getResource();
            Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), videoResource);

            UserMessage userMessage = UserMessage.builder()
                    .text(prompt)
                    .media(media)
                    .build();

            // 根据模式选择具体模型
            String targetModel = switch (mode.toLowerCase()) {
                case "thinking" -> thinkingModel;
                case "pro" -> proModel;
                default -> fastModel;
            };

            org.springframework.ai.google.genai.GoogleGenAiChatOptions options = org.springframework.ai.google.genai.GoogleGenAiChatOptions
                    .builder()
                    .model(targetModel)
                    .build();

            Prompt springAiPrompt = new Prompt(userMessage, options);
            log.info("已切换至模型: {}", targetModel);

            ChatResponse response = chatModel.call(springAiPrompt);

            String result = response.getResult().getOutput().getText();
            log.info("Gemini分析完成: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Gemini视频分析异常", e);
            if (e.getMessage() != null && e.getMessage().contains("429")) {
                return "分析失败: 当前请求并发或模型超出配额 (429 Too Many Requests)，建议尝试使用 fast (Flash) 模式重新提交。";
            }
            return "分析失败: " + e.getMessage();
        }
    }

    private final org.dromara.business.service.IBizPromptTemplateService promptTemplateService;

    @Override
    public String generateVeo3Json(String videoUrl, String productConfigJson, java.util.List<String> charImageUrls, java.util.List<String> productImageUrls, String mode) {
        try {
            if (org.apache.commons.lang3.StringUtils.isBlank(videoUrl)) {
                return "视频URL不能为空";
            }
            log.info("开始执行Veo3视频连环提示词分析任务: {}", videoUrl);

            // 1. 从接口查询 3 套提示词
            String prompt1 = getPromptByTemplateType(8L, "（缺失提示词1：veo3.1-短视频分镜逆向工程师）");
            String prompt2 = getPromptByTemplateType(9L, "（缺失提示词2：veo3.1-短视频8秒生成单元无损改写器）");            // 如果有配置，解析前端传来的JSON并替换模板中的 [填写] 占位符
            if (org.apache.commons.lang3.StringUtils.isNotBlank(productConfigJson)) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode config = mapper.readTree(productConfigJson);

                    String brandName = config.path("brandName").asText("");
                    String sellingPoints = config.path("sellingPoints").isArray()
                        ? String.join("，", mapper.convertValue(config.path("sellingPoints"), String[].class))
                        : config.path("sellingPoints").asText("");
                    String targetAudience = config.path("targetAudience").asText("");
                    String p1 = config.path("painPoints").isArray() && config.path("painPoints").size() > 0
                        ? config.path("painPoints").get(0).asText("")
                        : "";
                    String p2 = config.path("painPoints").isArray() && config.path("painPoints").size() > 1
                        ? config.path("painPoints").get(1).asText("")
                        : "";
                    String p3 = config.path("painPoints").isArray() && config.path("painPoints").size() > 2
                        ? config.path("painPoints").get(2).asText("")
                        : "";

                    prompt2 = prompt2.replace("品牌/产品名称: [填写]", "品牌/产品名称: " + brandName)
                        .replace("产品核心卖点: [填写，最多3条]", "产品核心卖点: " + sellingPoints)
                        .replace("目标用户群体: [填写]", "目标用户群体: " + targetAudience)
                        .replace("痛点一: [填写]", "痛点一: " + p1)
                        .replace("痛点二: [填写]", "痛点二: " + p2)
                        .replace("痛点三: [填写]", "痛点三: " + p3);
                    log.info("已成功将前端传入的JSON配置替换进第二步提示词模板中");
                } catch (Exception e) {
                    log.warn("无法解析产品配置JSON，忽略产品配置注入", e);
                }
            }
            String prompt3 = getPromptByTemplateType(10L, "（缺失提示词3：veo3.1-短视频8秒生成单元模板复刻导演）");



            // 根据模式选择具体模型
            String targetModel = switch (mode.toLowerCase()) {
                case "thinking" -> thinkingModel;
                case "pro" -> proModel;
                default -> fastModel;
            };
            org.springframework.ai.google.genai.GoogleGenAiChatOptions options = org.springframework.ai.google.genai.GoogleGenAiChatOptions
                    .builder()
                    .model(targetModel)
                    .build();

            // 准备历史上下文记忆列表
            List<org.springframework.ai.chat.messages.Message> chatHistory = new java.util.ArrayList<>();

            // ============================================
            // 环节 1: veo3.1-短视频分镜逆向工程师
            // ============================================
            log.info("--- 开始阶段1: 短视频分镜逆向 ---");
            org.springframework.core.io.Resource videoResource = new org.springframework.core.io.UrlResource(videoUrl);
            String mimeType = "video/mp4";
            if (videoUrl.toLowerCase().endsWith(".mov")) mimeType = "video/quicktime";
            Media media = new Media(MimeTypeUtils.parseMimeType(mimeType), videoResource);
            UserMessage userMessage1 = UserMessage.builder()
                    .text(prompt1)
                    .media(media)
                    .build();
            chatHistory.add(userMessage1);

            ChatResponse response1 = chatModel.call(new Prompt(chatHistory, options));
            org.springframework.ai.chat.messages.AssistantMessage assistantMessage1 = response1.getResult().getOutput();
            chatHistory.add(assistantMessage1);
            log.info("阶段1完成，结果长度: {}", assistantMessage1.getText().length());

            // ============================================
            // 环节 2: veo3.1-短视频8秒生成单元无损改写器
            // ============================================
            log.info("--- 开始阶段2: 短视频8秒生成单元无损改写 ---");
            UserMessage userMessage2 = new UserMessage(prompt2);
            chatHistory.add(userMessage2);

            ChatResponse response2 = chatModel.call(new Prompt(chatHistory, options));
            org.springframework.ai.chat.messages.AssistantMessage assistantMessage2 = response2.getResult().getOutput();
            chatHistory.add(assistantMessage2);
            log.info("阶段2完成，结果长度: {}", assistantMessage2.getText().length());

            // ============================================
            // 环节 3: veo3.1-短视频8秒生成单元模板复刻导演
            // ============================================
            log.info("--- 开始阶段3: 模板复刻导演生成JSON ---");
            String finalPrompt3 = prompt3;

            // 收集多模态参考图片 (输入B、输入C)
            List<Media> mediaList3 = new java.util.ArrayList<>();
            if (charImageUrls != null) {
                for (String url : charImageUrls) {
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                        mediaList3.add(new Media(MimeTypeUtils.IMAGE_PNG, new org.springframework.core.io.UrlResource(url)));
                    }
                }
            }
            if (productImageUrls != null) {
                for (String url : productImageUrls) {
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                        mediaList3.add(new Media(MimeTypeUtils.IMAGE_PNG, new org.springframework.core.io.UrlResource(url)));
                    }
                }
            }

            UserMessage userMessage3 = UserMessage.builder()
                    .text(finalPrompt3)
                    .media(mediaList3)
                    .build();
            chatHistory.add(userMessage3);

            org.springframework.ai.google.genai.GoogleGenAiChatOptions jsonOptions = org.springframework.ai.google.genai.GoogleGenAiChatOptions
                    .builder()
                    .model(targetModel)
                    .responseMimeType("application/json") // Gemini的强约束
                    .build();
            ChatResponse finalResponse = chatModel.call(new Prompt(chatHistory, jsonOptions));

            String finalJson = finalResponse.getResult().getOutput().getText();
            log.info("阶段3完成。");
            return finalJson;

        } catch (Exception e) {
            log.error("Gemini连环分析异常", e);
            if (e.getMessage() != null && e.getMessage().contains("429")) {
                throw new RuntimeException("分析失败: 当前模型超出配额 (429 Too Many Requests)，建议尝试切换至 fast (Flash) 模型重新提交。");
            }
            throw new RuntimeException("分析失败: " + e.getMessage());
        }
    }

    private String getPromptByTemplateType(Long templateType, String defaultPrompt) {
        org.dromara.business.domain.bo.BizPromptTemplateBo bo = new org.dromara.business.domain.bo.BizPromptTemplateBo();
        bo.setTemplateType(templateType);
        List<org.dromara.business.domain.vo.BizPromptTemplateVo> list = promptTemplateService.queryList(bo);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getTemplate();
        }
        return defaultPrompt;
    }

    @Override
    public String polishImage(String originalImageUrl, java.util.List<String> charImageUrls, java.util.List<String> productImageUrls) {
        try {
            log.info("开始AI洗图分析: {}", originalImageUrl);

            // 从模板获取洗图提示词（模板4：分析，模板5：生图指令）
            String analyzePrompt = getPromptByTemplateType(4L, "请分析这张图片中的人物特征、产品特征、场景构图、光线等关键信息，用于后续的图片重绘。");
            String generatePrompt = getPromptByTemplateType(5L, "基于上述分析结果，生成一段详细的图片重绘提示词（英文），要求保留原图构图和姿态，但替换人物和产品为参考图中的形象。");

//            String targetModel = proModel;
            String targetModel = fastModel;
            org.springframework.ai.google.genai.GoogleGenAiChatOptions options = org.springframework.ai.google.genai.GoogleGenAiChatOptions
                    .builder()
                    .model(targetModel)
                    .build();

            // 第一轮：分析原图 + 参考图
            List<Media> mediaList = new java.util.ArrayList<>();
            mediaList.add(new Media(MimeTypeUtils.IMAGE_PNG, new org.springframework.core.io.UrlResource(originalImageUrl)));
            if (charImageUrls != null) {
                for (String url : charImageUrls) {
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                        mediaList.add(new Media(MimeTypeUtils.IMAGE_PNG, new org.springframework.core.io.UrlResource(url)));
                    }
                }
            }
            if (productImageUrls != null) {
                for (String url : productImageUrls) {
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                        mediaList.add(new Media(MimeTypeUtils.IMAGE_PNG, new org.springframework.core.io.UrlResource(url)));
                    }
                }
            }

            List<org.springframework.ai.chat.messages.Message> history = new java.util.ArrayList<>();
            UserMessage msg1 = UserMessage.builder()
                    .text(analyzePrompt)
                    .media(mediaList)
                    .build();
            history.add(msg1);

            ChatResponse resp1 = chatModel.call(new Prompt(history, options));
            org.springframework.ai.chat.messages.AssistantMessage assist1 = resp1.getResult().getOutput();
            history.add(assist1);
            log.info("洗图分析阶段1完成，结果长度: {}", assist1.getText().length());

            // 第二轮：生成重绘提示词
            UserMessage msg2 = new UserMessage(generatePrompt);
            history.add(msg2);

            // 指定输出为json
            options.setResponseMimeType("application/json");
            ChatResponse resp2 = chatModel.call(new Prompt(history, options));
            String result = resp2.getResult().getOutput().getText();
            log.info("洗图分析阶段2完成");

            return result;
        } catch (Exception e) {
            log.error("AI洗图分析异常", e);
            throw new RuntimeException("洗图分析失败: " + e.getMessage());
        }
    }

    @org.springframework.beans.factory.annotation.Value("${spring.ai.google.genai.api-key}")
    private String apiKey;

    /**
     * 图片生成模型 (Nano Banana / Gemini Image)
     */
//    private static final String IMAGE_GEN_MODEL = "gemini-3.1-flash-image-preview";
    private static final String IMAGE_GEN_MODEL = "gemini-2.5-flash-image";
    private static final String GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";

    @Override
    public byte[] generateImage(String originalImageUrl, String finalPrompt) {
        try {
            log.info("开始Gemini图片生成: originalImageUrl={}", originalImageUrl);

            // 1. 下载原图并转为 base64
            byte[] imageBytes = downloadImage(originalImageUrl);
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
            String mimeType = guessMimeType(originalImageUrl);

            // 2. 构建 Gemini REST API 请求体
            // 参考: https://ai.google.dev/gemini-api/docs/image-generation?hl=zh-cn
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

            // 构建 parts 数组: [text_part, image_part]
            com.fasterxml.jackson.databind.node.ObjectNode textPart = mapper.createObjectNode();
            textPart.put("text", finalPrompt);

            com.fasterxml.jackson.databind.node.ObjectNode inlineData = mapper.createObjectNode();
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);
            com.fasterxml.jackson.databind.node.ObjectNode imagePart = mapper.createObjectNode();
            imagePart.set("inline_data", inlineData);

            com.fasterxml.jackson.databind.node.ArrayNode partsArray = mapper.createArrayNode();
            partsArray.add(textPart);
            partsArray.add(imagePart);

            com.fasterxml.jackson.databind.node.ObjectNode content = mapper.createObjectNode();
            content.set("parts", partsArray);

            com.fasterxml.jackson.databind.node.ArrayNode contentsArray = mapper.createArrayNode();
            contentsArray.add(content);

            // generationConfig: responseModalities = ["TEXT", "IMAGE"]
            com.fasterxml.jackson.databind.node.ObjectNode generationConfig = mapper.createObjectNode();
            com.fasterxml.jackson.databind.node.ArrayNode modalities = mapper.createArrayNode();
            modalities.add("TEXT");
            modalities.add("IMAGE");
            generationConfig.set("responseModalities", modalities);

            com.fasterxml.jackson.databind.node.ObjectNode requestBody = mapper.createObjectNode();
            requestBody.set("contents", contentsArray);
            requestBody.set("generationConfig", generationConfig);

            String jsonBody = mapper.writeValueAsString(requestBody);

            // 3. 调用 Gemini REST API
            String url = GEMINI_API_BASE + IMAGE_GEN_MODEL + ":generateContent";
            log.info("调用Gemini图片生成API: {}", url);

            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(30))
                    .build();

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .timeout(java.time.Duration.ofSeconds(120))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Gemini图片生成API返回错误, statusCode={}, body={}", response.statusCode(), response.body());
                throw new RuntimeException("Gemini图片生成失败, HTTP " + response.statusCode());
            }

            // 4. 解析响应，提取图片数据
            com.fasterxml.jackson.databind.JsonNode respRoot = mapper.readTree(response.body());
            com.fasterxml.jackson.databind.JsonNode candidates = respRoot.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                log.error("Gemini图片生成无候选结果, response={}", response.body());
                throw new RuntimeException("Gemini图片生成无候选结果");
            }

            com.fasterxml.jackson.databind.JsonNode parts = candidates.get(0).path("content").path("parts");
            for (com.fasterxml.jackson.databind.JsonNode part : parts) {
                com.fasterxml.jackson.databind.JsonNode partInlineData = part.path("inlineData");
                if (!partInlineData.isMissingNode() && partInlineData.has("data")) {
                    String imgBase64 = partInlineData.path("data").asText();
                    byte[] generatedImage = java.util.Base64.getDecoder().decode(imgBase64);
                    log.info("Gemini图片生成成功, 图片大小: {} bytes", generatedImage.length);
                    return generatedImage;
                }
            }

            // 如果没有找到图片，记录文本结果用于调试
            for (com.fasterxml.jackson.databind.JsonNode part : parts) {
                if (part.has("text")) {
                    log.warn("Gemini图片生成返回了文本而非图片: {}", part.path("text").asText());
                }
            }
            throw new RuntimeException("Gemini图片生成API未返回图片数据");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini图片生成异常", e);
            throw new RuntimeException("图片生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下载图片并返回字节数组
     */
    private byte[] downloadImage(String imageUrl) throws Exception {
        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(15))
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .build();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(imageUrl))
                .timeout(java.time.Duration.ofSeconds(30))
                .GET()
                .build();
        java.net.http.HttpResponse<byte[]> response = httpClient.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new RuntimeException("下载图片失败, HTTP " + response.statusCode() + ", url=" + imageUrl);
        }
        return response.body();
    }

    /**
     * 根据URL猜测MIME类型
     */
    private String guessMimeType(String url) {
        String lower = url.toLowerCase();
        if (lower.contains(".jpg") || lower.contains(".jpeg")) {
            return "image/jpeg";
        } else if (lower.contains(".webp")) {
            return "image/webp";
        } else {
            return "image/png";
        }
    }

    /**
     * Veo 3.1 视频生成模型
     */
    private static final String VEO_MODEL = "veo-3.1-generate-preview";
    private static final int VIDEO_POLL_INTERVAL_MS = 10_000; // 10秒轮询一次
    private static final int VIDEO_MAX_POLL_COUNT = 60;       // 最多轮询60次 = 10分钟

    @Override
    public byte[] generateVideoFromImage(String imageUrl, String prompt, java.util.List<String> referenceImageUrls) {
        try {
            log.info("开始Veo 3.1图生视频: imageUrl={}, prompt长度={}", imageUrl, prompt != null ? prompt.length() : 0);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

            // 1. 构建 instance 对象（只有 prompt + referenceImages）
            com.fasterxml.jackson.databind.node.ObjectNode instance = mapper.createObjectNode();
            instance.put("prompt", prompt);

            // 2. 构建 referenceImages 数组
            com.fasterxml.jackson.databind.node.ArrayNode refImagesArray = mapper.createArrayNode();

            // 2a. 将起始帧图片作为第一张参考图
            if (org.apache.commons.lang3.StringUtils.isNotBlank(imageUrl)) {
                try {
                    byte[] imageBytes = downloadImage(imageUrl);
                    String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                    String imageMimeType = guessMimeType(imageUrl);

                    com.fasterxml.jackson.databind.node.ObjectNode startInlineData = mapper.createObjectNode();
                    startInlineData.put("mimeType", imageMimeType);
                    startInlineData.put("data", base64Image);
                    com.fasterxml.jackson.databind.node.ObjectNode startImageNode = mapper.createObjectNode();
                    startImageNode.set("inlineData", startInlineData);

                    com.fasterxml.jackson.databind.node.ObjectNode startWrapper = mapper.createObjectNode();
                    startWrapper.set("image", startImageNode);
                    startWrapper.put("referenceType", "asset");

                    refImagesArray.add(startWrapper);
                    log.info("已添加起始帧图片作为参考图: {}", imageUrl);
                } catch (Exception e) {
                    log.warn("下载起始帧图片失败，跳过: {}", imageUrl, e);
                }
            }

            // 2b. 添加其他参考图片
            if (referenceImageUrls != null && !referenceImageUrls.isEmpty()) {
                for (String refUrl : referenceImageUrls) {
                    if (refImagesArray.size() >= 3) break; // Veo 3.1 最多支持3张参考图
                    if (org.apache.commons.lang3.StringUtils.isBlank(refUrl)) continue;
                    try {
                        byte[] refBytes = downloadImage(refUrl);
                        String refBase64 = java.util.Base64.getEncoder().encodeToString(refBytes);
                        String refMime = guessMimeType(refUrl);

                        com.fasterxml.jackson.databind.node.ObjectNode refInlineData = mapper.createObjectNode();
                        refInlineData.put("mimeType", refMime);
                        refInlineData.put("data", refBase64);
                        com.fasterxml.jackson.databind.node.ObjectNode refImageNode = mapper.createObjectNode();
                        refImageNode.set("inlineData", refInlineData);

                        com.fasterxml.jackson.databind.node.ObjectNode refImageWrapper = mapper.createObjectNode();
                        refImageWrapper.set("image", refImageNode);
                        refImageWrapper.put("referenceType", "asset");

                        refImagesArray.add(refImageWrapper);
                        log.info("已添加参考图 {}: {}", refImagesArray.size(), refUrl);
                    } catch (Exception e) {
                        log.warn("下载参考图失败，跳过: {}", refUrl, e);
                    }
                }
            }

            if (refImagesArray.size() > 0) {
                instance.set("referenceImages", refImagesArray);
            }

            // 4. 构建完整请求体
            com.fasterxml.jackson.databind.node.ArrayNode instancesArray = mapper.createArrayNode();
            instancesArray.add(instance);

            com.fasterxml.jackson.databind.node.ObjectNode requestBody = mapper.createObjectNode();
            requestBody.set("instances", instancesArray);

            String jsonBody = mapper.writeValueAsString(requestBody);

            // 5. 发送 predictLongRunning 请求
            String url = GEMINI_API_BASE + VEO_MODEL + ":predictLongRunning";
            log.info("调用Veo 3.1视频生成API: {}", url);

            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(30))
                    .build();

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .timeout(java.time.Duration.ofSeconds(120))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Veo 3.1视频生成API返回错误, statusCode={}, body={}", response.statusCode(), response.body());
                throw new RuntimeException("Veo 3.1视频生成失败, HTTP " + response.statusCode() + ": " + response.body());
            }

            // 6. 获取 operation name
            com.fasterxml.jackson.databind.JsonNode respRoot = mapper.readTree(response.body());
            String operationName = respRoot.path("name").asText();
            if (org.apache.commons.lang3.StringUtils.isBlank(operationName)) {
                log.error("Veo 3.1 未返回操作名称, response={}", response.body());
                throw new RuntimeException("Veo 3.1 未返回有效的操作名称");
            }
            log.info("Veo 3.1 视频生成任务已提交, operationName={}", operationName);

            // 7. 轮询操作状态
            String baseUrl = "https://generativelanguage.googleapis.com/v1beta";
            for (int i = 0; i < VIDEO_MAX_POLL_COUNT; i++) {
                Thread.sleep(VIDEO_POLL_INTERVAL_MS);

                java.net.http.HttpRequest pollRequest = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(baseUrl + "/" + operationName))
                        .header("x-goog-api-key", apiKey)
                        .timeout(java.time.Duration.ofSeconds(30))
                        .GET()
                        .build();

                java.net.http.HttpResponse<String> pollResponse = httpClient.send(pollRequest,
                        java.net.http.HttpResponse.BodyHandlers.ofString());

                if (pollResponse.statusCode() != 200) {
                    log.warn("轮询Veo状态时返回非200: statusCode={}, body={}", pollResponse.statusCode(), pollResponse.body());
                    continue;
                }

                com.fasterxml.jackson.databind.JsonNode pollRoot = mapper.readTree(pollResponse.body());
                boolean done = pollRoot.path("done").asBoolean(false);
                log.info("Veo 3.1 轮询第{}次, done={}", i + 1, done);

                if (done) {
                    // 检查是否有错误
                    com.fasterxml.jackson.databind.JsonNode errorNode = pollRoot.path("error");
                    if (!errorNode.isMissingNode() && errorNode.has("message")) {
                        String errorMsg = errorNode.path("message").asText();
                        log.error("Veo 3.1 视频生成失败: {}", errorMsg);
                        throw new RuntimeException("Veo 3.1 视频生成失败: " + errorMsg);
                    }

                    // 提取视频下载URI
                    String videoUri = pollRoot.path("response")
                            .path("generateVideoResponse")
                            .path("generatedSamples").get(0)
                            .path("video")
                            .path("uri").asText();

                    if (org.apache.commons.lang3.StringUtils.isBlank(videoUri)) {
                        log.error("Veo 3.1 视频生成完成但未返回视频URI, response={}", pollResponse.body());
                        throw new RuntimeException("Veo 3.1 视频生成完成但无视频URI");
                    }

                    log.info("Veo 3.1 视频生成完成, 开始下载: {}", videoUri);

                    // 8. 下载视频（需要带上 API key）
                    java.net.http.HttpRequest downloadRequest = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create(videoUri))
                            .header("x-goog-api-key", apiKey)
                            .timeout(java.time.Duration.ofSeconds(120))
                            .GET()
                            .build();

                    java.net.http.HttpResponse<byte[]> videoResponse = httpClient.send(downloadRequest,
                            java.net.http.HttpResponse.BodyHandlers.ofByteArray());

                    // 处理重定向（如果有）
                    if (videoResponse.statusCode() == 302 || videoResponse.statusCode() == 301) {
                        String redirectUrl = videoResponse.headers().firstValue("Location").orElse("");
                        if (!redirectUrl.isEmpty()) {
                            downloadRequest = java.net.http.HttpRequest.newBuilder()
                                    .uri(java.net.URI.create(redirectUrl))
                                    .header("x-goog-api-key", apiKey)
                                    .timeout(java.time.Duration.ofSeconds(120))
                                    .GET()
                                    .build();
                            videoResponse = httpClient.send(downloadRequest,
                                    java.net.http.HttpResponse.BodyHandlers.ofByteArray());
                        }
                    }

                    if (videoResponse.statusCode() != 200) {
                        throw new RuntimeException("下载Veo视频失败, HTTP " + videoResponse.statusCode());
                    }

                    byte[] videoBytes = videoResponse.body();
                    log.info("Veo 3.1 视频下载完成, 大小: {} bytes ({}MB)", videoBytes.length,
                            String.format("%.2f", videoBytes.length / 1024.0 / 1024.0));
                    return videoBytes;
                }
            }

            throw new RuntimeException("Veo 3.1 视频生成超时（等待超过" + (VIDEO_MAX_POLL_COUNT * VIDEO_POLL_INTERVAL_MS / 1000) + "秒）");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Veo 3.1 图生视频异常", e);
            throw new RuntimeException("图生视频失败: " + e.getMessage(), e);
        }
    }
}
