package org.dromara.business.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.domain.BizVideoReproduceFrame;
import org.dromara.business.domain.BizVideoReproduceTask;
import org.dromara.business.domain.bo.BizVideoReproduceTaskBo;
import org.dromara.business.domain.vo.BizVideoReproduceTaskVo;
import org.dromara.business.mapper.BizVideoReproduceFrameMapper;
import org.dromara.business.mapper.BizVideoReproduceTaskMapper;
import org.dromara.business.service.IBizGeminiVideoService;
import org.dromara.business.service.IBizVideoReproduceService;
import org.dromara.common.core.domain.dto.OssDTO;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.OssService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 视频复刻任务Service业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizVideoReproduceServiceImpl implements IBizVideoReproduceService {

    private final BizVideoReproduceTaskMapper taskMapper;
    private final BizVideoReproduceFrameMapper frameMapper;
    private final IBizGeminiVideoService geminiVideoService;
    private final ChatModel chatModel;
    private final OssService ossService;
    private final org.dromara.business.service.IBizLocalTaskService localTaskService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TableDataInfo<BizVideoReproduceTaskVo> queryPageList(BizVideoReproduceTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizVideoReproduceTask> lqw = new LambdaQueryWrapper<>();
        // 简单列表查询
        lqw.orderByDesc(BizVideoReproduceTask::getCreateTime);
        Page<BizVideoReproduceTaskVo> result = taskMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public BizVideoReproduceTaskVo createAndStartTask(MultipartFile videoFile, String productConfigJson, MultipartFile[] charImages,
            MultipartFile[] productImages, String execMode) {
        // 1. 上传视频
        String originalVideoUrl = "";
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                File tempVideo = saveToTemp(videoFile);
                try {
                    OssDTO oss = ossService.uploadFiled(tempVideo);
                    originalVideoUrl = oss.getUrl();
                } finally {
//                    cn.hutool.core.io.FileUtil.del(tempVideo);
                }
            } catch (Exception e) {
                log.error("视频上传失败", e);
            }
        }

        // 2. 保存参考图片到OSS并转为JSON字符串
        String charImagesJson = uploadAndConvertToJson(charImages);
        String productImagesJson = uploadAndConvertToJson(productImages);

        // 3. 创建任务记录
        BizVideoReproduceTask task = new BizVideoReproduceTask();
        task.setTaskId(IdUtil.getSnowflakeNextId());
        task.setOriginalVideoUrl(originalVideoUrl);

        // 处理如果是空字符串的情况，避免 MySQL JSON 类型报错
        if (cn.hutool.core.util.StrUtil.isBlank(productConfigJson)) {
            task.setProductConfigJson(null);
        } else {
            task.setProductConfigJson(productConfigJson);
        }

        task.setCharImages(charImagesJson);
        task.setProductImages(productImagesJson);
        task.setStatus("0"); // 待开始
        task.setCreateTime(new Date());
        taskMapper.insert(task);

        // 4. 异步开始流程
        if ("local".equalsIgnoreCase(execMode)) {
            startLocalAnalyzeWorkflow(task.getTaskId());
        } else {
            startFullWorkflow(task.getTaskId());
        }

        return taskMapper.selectVoById(task.getTaskId());
    }

    @Async
    public void startLocalAnalyzeWorkflow(Long taskId) {
        BizVideoReproduceTask task = taskMapper.selectById(taskId);
        if (task == null) return;

        updateTaskStatus(taskId, "10", "任务已下发至本地云端队列，等待执行中...");
        log.info("任务 {}: 进入本地云端分析队列", taskId);

        // 生成提示词
        java.util.List<String> prompts = geminiVideoService.getVeo3Prompts(task.getProductConfigJson());

        // 构建参数发给 by-chrome-app
        org.dromara.business.domain.BizLocalTask localTask = new org.dromara.business.domain.BizLocalTask();
        localTask.setTaskType("ANALYZE_VIDEO");
        localTask.setRefTaskId(taskId);
        localTask.setStatus(0);
        localTask.setCreateTime(new Date());

        try {
            com.fasterxml.jackson.databind.node.ObjectNode params = objectMapper.createObjectNode();
            params.put("videoUrl", task.getOriginalVideoUrl());

            com.fasterxml.jackson.databind.node.ArrayNode charArray = params.putArray("charUrls");
            parseJsonList(task.getCharImages()).forEach(charArray::add);

            com.fasterxml.jackson.databind.node.ArrayNode prodArray = params.putArray("productUrls");
            parseJsonList(task.getProductImages()).forEach(prodArray::add);

            com.fasterxml.jackson.databind.node.ArrayNode promptsArray = params.putArray("prompts");
            prompts.forEach(promptsArray::add);

            localTask.setExecParams(objectMapper.writeValueAsString(params));

            org.dromara.business.mapper.BizLocalTaskMapper localTaskMapper =
                 org.dromara.common.core.utils.SpringUtils.getBean(org.dromara.business.mapper.BizLocalTaskMapper.class);
            localTaskMapper.insert(localTask);
        } catch (Exception e) {
            log.error("插入本地分析任务失败", e);
            updateTaskStatus(taskId, "9", "本地发单失败: " + e.getMessage());
        }
    }

    @Async
    public void startFullWorkflow(Long taskId) {
        try {
            BizVideoReproduceTask task = taskMapper.selectById(taskId);
            if (task == null) return;
            String productConfigJson = task.getProductConfigJson();

            updateTaskStatus(taskId, "1", "正在使用Gemini分析视频及其三套提示词...");

            List<String> charUrls = parseJsonList(task.getCharImages());
            List<String> productUrls = parseJsonList(task.getProductImages());

            // 第一阶段：Gemini三连发分析
            String resultJson = geminiVideoService.generateVeo3Json(task.getOriginalVideoUrl(), productConfigJson, charUrls,
                    productUrls, "fast");

            continueFullWorkflowAfterAnalysis(taskId, resultJson);

        } catch (Exception e) {
            log.error("视频复刻任务处理失败, taskId: {}", taskId, e);
            BizVideoReproduceTask errorUpdate = new BizVideoReproduceTask();
            errorUpdate.setTaskId(taskId);
            errorUpdate.setStatus("9");
            errorUpdate.setErrorMsg(e.getMessage());
            taskMapper.updateById(errorUpdate);
        }
    }

    @Override
    public void continueFullWorkflowAfterAnalysis(Long taskId, String resultJson) {
        try {
            BizVideoReproduceTask task = taskMapper.selectById(taskId);
            if (task == null) return;

            // 解析全局锁
            JsonNode root = objectMapper.readTree(resultJson);
            JsonNode globalLockNode = root.path("global_lock_card");

            BizVideoReproduceTask taskUpdate = new BizVideoReproduceTask();
            taskUpdate.setTaskId(taskId);
            taskUpdate.setResultJson(resultJson);
            if (globalLockNode.isMissingNode() || globalLockNode.isNull()) {
                taskUpdate.setGlobalLocks(null);
            } else {
                taskUpdate.setGlobalLocks(globalLockNode.toString());
            }
            taskUpdate.setStatus("2");
            taskMapper.updateById(taskUpdate);

            // 第二阶段：截帧
            log.info("任务 {}: 开始截取关键帧...", taskId);
            updateTaskStatus(taskId, "2", "正在解析JSON并截取关键帧...");

            JsonNode guPrompts = root.path("gu_prompts");
            if (guPrompts.isArray()) {
                File tempVideo = saveToTemp(task.getOriginalVideoUrl());
                try {
                    for (JsonNode gu : guPrompts) {
                        String guId = gu.path("gu_id").asText();
                        double timestamp = gu.path("reference_frame_info").path("timestamp_sec").asDouble();

                        // 截帧逻辑
                        String framePath = extractFrame(tempVideo.getAbsolutePath(), timestamp);
                        File frameFile = new File(framePath);

                        // 上传原截帧到OSS
                        OssDTO frameOss = ossService.uploadFiled(frameFile);

                        // 保存截帧记录
                        BizVideoReproduceFrame frameRecord = new BizVideoReproduceFrame();
                        frameRecord.setFrameId(IdUtil.getSnowflakeNextId());
                        frameRecord.setTaskId(taskId);
                        frameRecord.setGuId(guId);
                        frameRecord.setTimestampSec(String.valueOf(timestamp));
                        frameRecord.setOriginalImageUrl(frameOss.getUrl());
                        frameRecord.setStatus("1"); // 截帧成功
                        frameRecord.setCreateTime(new Date());

                        // 在截帧阶段就组合并保存完整的图生视频提示词
                        String i2vPrompt = composeFinalI2vPrompt(task, frameRecord, root);
                        frameRecord.setI2vPromptEn(i2vPrompt);
                        // 同时保存中文对照提示词
                        String i2vPromptZh = composeFinalI2vPromptZh(frameRecord, root);
                        frameRecord.setI2vPromptZh(i2vPromptZh);
                        frameMapper.insert(frameRecord);

                        FileUtil.del(frameFile);
                    }
                } finally {
                    FileUtil.del(tempVideo);
                }
            }
            // 第二阶段：截帧完成
            log.info("任务 {}: 截帧完成，等待用户手动洗图...", taskId);
            updateTaskStatus(taskId, "3", "截帧完成，等待洗图");

        } catch (Exception e) {
            log.error("视频复刻后续处理失败, taskId: {}", taskId, e);
            BizVideoReproduceTask errorUpdate = new BizVideoReproduceTask();
            errorUpdate.setTaskId(taskId);
            errorUpdate.setStatus("9");
            errorUpdate.setErrorMsg("提取截帧失败: " + e.getMessage());
            taskMapper.updateById(errorUpdate);
        }
    }

    private String uploadAndConvertToJson(MultipartFile[] files) {
        if (files == null || files.length == 0)
            return "[]";
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    File temp = saveToTemp(file);
                    try {
                        OssDTO oss = ossService.uploadFiled(temp);
                        arrayNode.add(oss.getUrl());
                    } finally {
                        cn.hutool.core.io.FileUtil.del(temp);
                    }
                } catch (Exception e) {
                    log.error("文件上传失败", e);
                }
            }
        }
        return arrayNode.toString();
    }

    @Override
    public void bindAudio(Long frameId, MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) return;
        try {
            File tempAudio = saveToTemp(audioFile);
            OssDTO oss = ossService.uploadFiled(tempAudio);
            BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
            frame.setAudioUrl(oss.getUrl());
            frameMapper.updateById(frame);
        } catch (Exception e) {
            log.error("音频绑定失败", e);
            throw new ServiceException("音频上传失败: " + e.getMessage());
        }
    }

    @Override
    public void autoTrimAudio(Long frameId) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null || cn.hutool.core.util.StrUtil.isBlank(frame.getAudioUrl())) {
            throw new ServiceException("未找到待处理的音频");
        }

        String workDir = "C:\\Users\\CuiMa\\Downloads\\capture_frames\\audio_trim_" + frameId;
        cn.hutool.core.io.FileUtil.mkdir(workDir);
        String inputPath = workDir + "\\input.mp3";
        String outputPath = workDir + "\\output.mp3";

        try {
            downloadFile(frame.getAudioUrl(), inputPath);
            String ffmpegPath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe";

            // 自动移除前后静音：使用 silenceremove 滤镜，先正序移除头部，再倒序重复移除新头部（即尾部），最后回正
            String[] commonArgs = {
                ffmpegPath, "-y", "-i", inputPath,
                "-af", "silenceremove=start_periods=1:start_silence=0.1:start_threshold=-45dB,areverse,silenceremove=start_periods=1:start_silence=0.1:start_threshold=-45dB,areverse",
                outputPath
            };

            runFfmpeg(commonArgs);

            OssDTO oss = ossService.uploadFiled(new File(outputPath));
            frame.setAudioUrl(oss.getUrl());
            frameMapper.updateById(frame);
        } catch (Exception e) {
             throw new ServiceException("音频自动优化失败: " + e.getMessage());
        } finally {
             cn.hutool.core.io.FileUtil.del(workDir);
        }
    }

    @Override
    public void manualTrimAudio(Long frameId, Double start, Double end) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null || cn.hutool.core.util.StrUtil.isBlank(frame.getAudioUrl())) {
            throw new ServiceException("未找到待处理的音频");
        }

        String workDir = "C:\\Users\\CuiMa\\Downloads\\capture_frames\\audio_manual_" + frameId;
        cn.hutool.core.io.FileUtil.mkdir(workDir);
        String inputPath = workDir + "\\input.mp3";
        String outputPath = workDir + "\\output.mp3";

        try {
            downloadFile(frame.getAudioUrl(), inputPath);
            String ffmpegPath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe";

            // -ss 在 -i 之前表示快速定位，-t 表示持续时长
            String[] args = {
                ffmpegPath, "-y", "-ss", String.format("%.3f", start), "-t", String.format("%.3f", end - start),
                "-i", inputPath, "-c", "copy", outputPath
            };

            runFfmpeg(args);

            OssDTO oss = ossService.uploadFiled(new File(outputPath));
            frame.setAudioUrl(oss.getUrl());
            frameMapper.updateById(frame);
        } catch (Exception e) {
             throw new ServiceException("手动裁剪失败: " + e.getMessage());
        } finally {
             cn.hutool.core.io.FileUtil.del(workDir);
        }
    }

    @Override
    public void syncAudioToVideo(Long frameId) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null || cn.hutool.core.util.StrUtil.isBlank(frame.getAudioUrl()) || cn.hutool.core.util.StrUtil.isBlank(frame.getGeneratedVideoUrl())) {
             throw new ServiceException("同步前请确保：1.已生成视频 2.已上传或裁剪好音频");
        }

        String workDir = "C:\\Users\\CuiMa\\Downloads\\capture_frames\\sync_" + frameId;
        cn.hutool.core.io.FileUtil.mkdir(workDir);
        String videoSourcePath = workDir + "\\video_src.mp4";
        String audioSourcePath = workDir + "\\audio_src.mp3";
        String outputPath = workDir + "\\video_synced.mp4";

        try {
            downloadFile(frame.getGeneratedVideoUrl(), videoSourcePath);
            downloadFile(frame.getAudioUrl(), audioSourcePath);

            String ffmpegPath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe";

            // 核心逻辑：替换音频并对齐。
            // 采用 -map 0:v -map 1:a 替换轨道，-shortest 以最短的（通常是视频）为准
            // 如果需要口型对准，Gemini Veo生成的视频往往没有声音或声音不匹配，这里强制覆盖音频流。
            String[] args = {
                ffmpegPath, "-y", "-i", videoSourcePath, "-i", audioSourcePath,
                "-map", "0:v:0", "-map", "1:a:0",
                "-c:v", "copy", "-c:a", "aac", "-b:a", "192k",
                "-shortest", outputPath
            };

            runFfmpeg(args);

            OssDTO oss = ossService.uploadFiled(new File(outputPath));
            // 覆盖原生成的视频
            frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
            frame.setGeneratedVideoUrl(oss.getUrl());
            frameMapper.updateById(frame);
        } catch (Exception e) {
             throw new ServiceException("音画同步失败: " + e.getMessage());
        } finally {
             cn.hutool.core.io.FileUtil.del(workDir);
        }
    }

    private void runFfmpeg(String[] args) throws Exception {
        log.info("Executing FFmpeg: {}", String.join(" ", args));
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("FFmpeg Log: {}", line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg执行异常，退出码: " + exitCode);
        }
    }

    private void downloadFile(String url, String localPath) throws Exception {
        cn.hutool.http.HttpUtil.downloadFile(url, localPath);
    }

    private void updateTaskStatus(Long taskId, String status, String msg) {
        BizVideoReproduceTask task = new BizVideoReproduceTask();
        task.setTaskId(taskId);
        task.setStatus(status);
        task.setErrorMsg(msg);
        taskMapper.updateById(task);
    }

    private File saveToTemp(MultipartFile file) throws Exception {
        File temp = File.createTempFile("video_", "_" + file.getOriginalFilename());
        file.transferTo(temp);
        return temp;
    }

    private File saveToTemp(String url) throws Exception {
        if (org.apache.commons.lang3.StringUtils.isBlank(url)) return null;
        File temp = File.createTempFile("video_dl_", ".mp4");
        cn.hutool.http.HttpUtil.downloadFile(url, temp);
        return temp;
    }

    private List<String> parseJsonList(String json) {
        List<String> list = new ArrayList<>();
        try {
            if (org.apache.commons.lang3.StringUtils.isBlank(json)) return list;
            JsonNode node = objectMapper.readTree(json);
            if (node.isArray()) {
                for (JsonNode n : node) {
                    list.add(n.asText());
                }
            }
        } catch (Exception e) {
            log.warn("解析URL JSON失败: {}", json);
        }
        return list;
    }

    private String extractFrame(String videoPath, double timestamp) throws Exception {
        String outPath = videoPath + "_" + timestamp + ".png";
        // 使用 ProcessBuilder 分离参数，避免 Runtime.exec(String) 在 Windows 上路径解析问题
        String ffmpegPath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe";
        ProcessBuilder pb = new ProcessBuilder(
            ffmpegPath, "-y", "-ss", String.format("%.3f", timestamp),
            "-i", videoPath, "-vframes", "1", "-q:v", "2", outPath
        );
        pb.redirectErrorStream(true); // 合并 stderr 到 stdout，防止缓冲区满导致死锁
        Process process = pb.start();
        // 消费输出流，防止进程阻塞
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("ffmpeg: {}", line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg 截帧失败, exitCode: " + exitCode);
        }
        return outPath;
    }

    /**
     * AI洗图：使用Google图像模型（Nano Banana代指）
     */
    private String washImageViaApi(String originalImageUrl, List<Media> references) throws Exception {
        log.info("正在对图片进行AI洗图: {}", originalImageUrl);

        // 构造提示词
        String polishPrompt = "You are a professional image polisher (Nano Banana Engine). " +
                "I have an original video frame and several reference images (Character identity and Product identity). "
                +
                "Task: Redraw the provided video frame to ensure the character matches the reference character and the product matches the reference product. "
                +
                "Keep the same pose, lighting, and composition as the original frame, but transform the details to match the reference materials. "
                +
                "Output ONLY the polished image.";

        // 将原图也加入Media列表
        List<Media> allMedias = new ArrayList<>(references);
        allMedias
                .add(new Media(MimeTypeUtils.IMAGE_PNG, new org.springframework.core.io.UrlResource(originalImageUrl)));

        UserMessage userMessage = UserMessage.builder()
                .text(polishPrompt)
                .media(allMedias)
                .build();

        // 这里的逻辑特殊：我们需要支持多模态输出的模型。由于目前Spring AI封装限制，
        // 我们假设通过特定的模型ID指令（如Nano Banana Pro）调用，并从结果中获取图片。
        // 如果API返回的是文字描述的URL，直接采集。如果是二进制，需要上传到OSS。

        // 注意：目前 Google Vertex AI / AI Studio 的 API 正在快速演进。
        // 部分模型支持在 ChatResponse 中返回 Resource。

        ChatResponse response = chatModel.call(new Prompt(userMessage));
        String textResult = response.getResult().getOutput().getText();

        // 尝试从文本中寻找生成的图片URL（如果模型返回了URL）
        // 或者是如果是多模态输出，可能会在 Media 中
        if (textResult.contains("http")) {
            // 简单提取第一个URL
            int start = textResult.indexOf("http");
            int end = textResult.indexOf(" ", start);
            if (end == -1)
                end = textResult.length();
            return textResult.substring(start, end).trim();
        }

        // 兜底：暂时返回原图，实际需根据具体Nano Banana API文档解析二进制返回
        log.warn("洗图API未返回可识别的图像URL，暂时使用原图。结果: {}", textResult);
        return originalImageUrl;
    }

    private String composeFinalI2vPrompt(BizVideoReproduceTask task, BizVideoReproduceFrame frame, JsonNode root) {
        StringBuilder sb = new StringBuilder();
//        sb.append("以下为本单元图生视频提示词：\n\n");

        // 获取全局锁
        JsonNode locks = root.path("global_lock_card");
        sb.append("全片统一锁\n");
        sb.append("人物锁：").append(locks.path("character_lock").asText()).append("\n");
        sb.append("商品锁：").append(locks.path("product_lock").asText()).append("\n");
        sb.append("禁包装锁：").append(locks.path("no_packaging_lock").asText()).append("\n");
        sb.append("画面统一锁：").append(locks.path("visual_consistency_lock").asText()).append("\n");
        sb.append("语音锁：").append(locks.path("voice_lock").asText()).append("\n");
        sb.append("音画模式锁：").append(locks.path("audio_visual_mode_lock").asText()).append("\n");
        sb.append("其他锁：不能出现任何字幕\n");
        sb.append("尾段锁：").append(locks.path("tail_lock").asText()).append("\n\n");

        // 获取该GU的具体内容
        JsonNode guPrompts = root.path("gu_prompts");
        for (JsonNode gu : guPrompts) {
            if (gu.path("gu_id").asText().equals(frame.getGuId())) {
                JsonNode enPromptNode = gu.path("i2v_prompt_for_model_en");
                sb.append("画面 / 台词 / 音效\n");
                sb.append(enPromptNode.path("visual_dialogue_sfx").asText());
                break;
            }
        }

        return sb.toString();
    }

    /**
     * 提取中文对照提示词 (i2v_prompt_zh_check.visual_dialogue_sfx)
     */
    private String composeFinalI2vPromptZh(BizVideoReproduceFrame frame, JsonNode root) {
        JsonNode guPrompts = root.path("gu_prompts");
        for (JsonNode gu : guPrompts) {
            if (gu.path("gu_id").asText().equals(frame.getGuId())) {
                JsonNode zhPromptNode = gu.path("i2v_prompt_zh_check");
                return zhPromptNode.path("visual_dialogue_sfx").asText("");
            }
        }
        return "";
    }

    private void addImagesToMediaList(String imageUrlsJson, List<Media> list) {
        try {
            JsonNode array = objectMapper.readTree(imageUrlsJson);
            if (array.isArray()) {
                for (JsonNode url : array) {
                    list.add(new Media(MimeTypeUtils.IMAGE_PNG,
                            new org.springframework.core.io.UrlResource(url.asText())));
                }
            }
        } catch (Exception e) {
            log.warn("解析参考图失败", e);
        }
    }

    @Override
    public void retryTask(Long taskId) {
        BizVideoReproduceTask task = taskMapper.selectById(taskId);
        // ... 实现重新触发逻辑
    }

    @Override
    public void generateAllVideos(Long taskId, String execMode) {
        if ("local".equalsIgnoreCase(execMode)) {
            List<BizVideoReproduceFrame> frames = getFrames(taskId);
            for (BizVideoReproduceFrame frame : frames) {
                // 如果还未生成视频，则推入本地队里
                if (cn.hutool.core.util.StrUtil.isBlank(frame.getGeneratedVideoUrl())) {
                    java.util.Map<String, Object> params = new java.util.HashMap<>();
                    params.put("frameId", frame.getFrameId());
                    params.put("startImageUrl", cn.hutool.core.util.StrUtil.isNotBlank(frame.getPolishedImageUrl()) ? frame.getPolishedImageUrl() : frame.getOriginalImageUrl());
                    params.put("prompt", frame.getI2vPromptEn());
                    try {
                        localTaskService.enqueueTask("GEN_VIDEO", taskId, frame.getFrameId(), objectMapper.writeValueAsString(params));
                    } catch (Exception e) {}
                }
            }
            return;
        }
        // ... 实现调用 Veo 3.1 批量生成的逻辑
    }

    @Override
    public List<BizVideoReproduceFrame> getFrames(Long taskId) {
        return frameMapper.selectList(new LambdaQueryWrapper<BizVideoReproduceFrame>()
                .eq(BizVideoReproduceFrame::getTaskId, taskId)
                .orderByAsc(BizVideoReproduceFrame::getGuId));
    }

    @Override
    public void washImage(Long frameId, String washMode, String customPrompt, List<String> refImages, String execMode) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null) {
            throw new ServiceException("截帧不存在");
        }
        BizVideoReproduceTask task = taskMapper.selectById(frame.getTaskId());

        try {
            // 解析参考图
            List<String> charUrls = parseJsonList(task.getCharImages());
            List<String> productUrls = parseJsonList(task.getProductImages());
            List<String> combinedUrls = new ArrayList<>();
            combinedUrls.addAll(charUrls);
            combinedUrls.addAll(productUrls);
            if (refImages != null) {
                combinedUrls.addAll(refImages);
            }

            // 更新DB状态
            frame.setWashMode(washMode);
            frame.setWashCustomPrompt(customPrompt);
            if (refImages != null) {
                frame.setWashRefImages(objectMapper.writeValueAsString(refImages));
            }

            // 此处由于Gemini API我们还没有把所有模式完全映射到底层，
            // 暂时调用 geminiVideoService.polishImage 统一处理
            // (TODO: 内部可以根据 washMode 和 customPrompt 精细化组装提示词)
            String targetImgUrl = "restyled".equals(washMode) || "restyled_pure".equals(washMode)
                    ? (frame.getPolishedImageUrl() != null ? frame.getPolishedImageUrl() : frame.getOriginalImageUrl())
                    : frame.getOriginalImageUrl();

            // 存入历史以支持撤回 (仅保存真正不同的)
            if (frame.getPolishedImageUrl() != null && !frame.getPolishedImageUrl().equals(targetImgUrl)) {
                frame.setPrevPolishedUrl(frame.getPolishedImageUrl());
            }

            if ("local".equalsIgnoreCase(execMode)) {
                java.util.Map<String, Object> params = new java.util.HashMap<>();
                params.put("frameId", frameId);

                // 1. 映射模式名称 (转化为 automation 识别的 smart/pure 标识)
                String normalizedMode = washMode;
                if ("original".equals(washMode)) normalizedMode = "original_smart";
                else if ("restyled".equals(washMode)) normalizedMode = "restyled_smart";
                params.put("mode", normalizedMode);

                // 2. 注入提示词模板
                params.put("templates", geminiVideoService.getImageWashTemplates());

                // 3. 注入自定义提示词
                params.put("customPrompt", customPrompt);

                // 4. 注入图片上下文
                params.put("sourceUrl", targetImgUrl);
                params.put("charUrls", charUrls);      // 基础人物参考
                params.put("productUrls", productUrls); // 基础商品参考
                params.put("extraMaterials", refImages); // 素材库选中的参考图

                localTaskService.enqueueTask("WASH_IMAGE", task.getTaskId(), frameId, objectMapper.writeValueAsString(params));
                frameMapper.updateById(frame);
                log.info("洗图本地任务已入队: frameId={}, mode={}, sourceUrl={}", frameId, normalizedMode, targetImgUrl);
                return;
            }

            // 真正发起API请求：阶段1+2 获取洗图提示词JSON
            String resultJson = geminiVideoService.polishImage(targetImgUrl, charUrls, productUrls);
            log.info("截帧ID {} 洗图提示词获取完成", frame.getFrameId());

            // 解析 final_prompt
            String finalPrompt;
            try {
                JsonNode resultNode = objectMapper.readTree(resultJson);
                finalPrompt = resultNode.path("final_prompt").asText(null);
                if (finalPrompt == null || finalPrompt.isBlank()) {
                    // 兜底：如果JSON解析不到 final_prompt，尝试直接使用整段文本
                    log.warn("截帧ID {} polishImage返回的JSON中没有final_prompt字段，尝试使用全文", frame.getFrameId());
                    finalPrompt = resultJson;
                }
            } catch (Exception parseEx) {
                // 如果不是JSON格式，直接当做提示词使用
                log.warn("截帧ID {} polishImage返回结果非JSON格式，直接作为提示词使用", frame.getFrameId());
                finalPrompt = resultJson;
            }

            // 阶段3：调用 Gemini 图片生成模型，上传原图+提示词，获取生成图片
            byte[] generatedImageBytes = geminiVideoService.generateImage(targetImgUrl, finalPrompt);

            // 保存生成的图片到临时文件并上传OSS
            String suffix = ".png";
            File tempFile = FileUtil.createTempFile("wash_" + frame.getFrameId() + "_", suffix, true);
            FileUtil.writeBytes(generatedImageBytes, tempFile);
            try {
                OssDTO ossResult = ossService.uploadFiled(tempFile);
                frame.setPolishedImageUrl(ossResult.getUrl());
                log.info("截帧ID {} 洗图完成，生成图片已上传OSS: {}", frame.getFrameId(), ossResult.getUrl());
            } finally {
                FileUtil.del(tempFile);
            }

            // i2v提示词已在截帧阶段保存，洗图阶段只处理图片
            frame.setStatus("2"); // 洗图完成
            frameMapper.updateById(frame);


        } catch (Exception e) {
            log.error("截帧ID {} 洗图失败", frame.getFrameId(), e);
            throw new ServiceException("洗图失败: " + e.getMessage());
        }
    }

    @Override
    public void washAllImages(Long taskId, String washMode, String customPrompt, List<String> refImages, String execMode) {
        List<BizVideoReproduceFrame> frames = getFrames(taskId);
        for (BizVideoReproduceFrame frame : frames) {
            washImage(frame.getFrameId(), washMode, customPrompt, refImages, execMode);
        }
        updateTaskStatus(taskId, "4", "完成");
    }

    @Override
    public void undoWash(Long frameId) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame != null && cn.hutool.core.util.StrUtil.isNotBlank(frame.getPrevPolishedUrl())) {
            String temp = frame.getPolishedImageUrl();
            frame.setPolishedImageUrl(frame.getPrevPolishedUrl());
            frame.setPrevPolishedUrl(temp);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void generateVideo(Long frameId, String execMode) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null) {
            throw new ServiceException("截帧不存在");
        }

        BizVideoReproduceTask task = taskMapper.selectById(frame.getTaskId());
        if (task == null) {
            throw new ServiceException("关联任务不存在");
        }

        try {
            // 1. 确定起始帧图片：优先使用洗图后的图片，没有则用原截帧
            String startImageUrl = cn.hutool.core.util.StrUtil.isNotBlank(frame.getPolishedImageUrl())
                    ? frame.getPolishedImageUrl()
                    : frame.getOriginalImageUrl();

            if (cn.hutool.core.util.StrUtil.isBlank(startImageUrl)) {
                throw new ServiceException("截帧无可用图片（原图和洗图均为空）");
            }

            // 2. 获取提示词
            String prompt = frame.getI2vPromptEn();
            if (cn.hutool.core.util.StrUtil.isBlank(prompt)) {
                throw new ServiceException("该截帧缺少图生视频提示词（i2vPromptEn）");
            }

            log.info("触发单帧生视频, FrameID: {}, 起始帧: {}, Prompt长度: {}", frameId, startImageUrl, prompt.length());

            // 3. 收集参考图片（人物参考图 + 商品参考图，最多3张）
            List<String> referenceUrls = new ArrayList<>();
//            List<String> charUrls = parseJsonList(task.getCharImages());
//            List<String> productUrls = parseJsonList(task.getProductImages());
//            referenceUrls.addAll(charUrls);
//            referenceUrls.addAll(productUrls);

            // 4. 存入历史以支持撤回
            if (cn.hutool.core.util.StrUtil.isNotBlank(frame.getGeneratedVideoUrl())) {
                frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
            }

            if ("local".equalsIgnoreCase(execMode)) {
                java.util.Map<String, Object> params = new java.util.HashMap<>();
                params.put("frameId", frameId);
                params.put("startImageUrl", startImageUrl);
                params.put("prompt", prompt);
                params.put("referenceUrls", referenceUrls);
                localTaskService.enqueueTask("GEN_VIDEO", task.getTaskId(), frameId, objectMapper.writeValueAsString(params));
                frameMapper.updateById(frame);
                log.info("视频生成本地任务已入队: frameId={}", frameId);
                return;
            }

            // 5. 调用 Veo 3.1 API 生成视频
            byte[] videoBytes = geminiVideoService.generateVideoFromImage(startImageUrl, prompt, referenceUrls);

            // 6. 保存生成的视频到临时文件并上传OSS
            File tempFile = FileUtil.createTempFile("veo_" + frame.getFrameId() + "_", ".mp4", true);
            FileUtil.writeBytes(videoBytes, tempFile);
            try {
                OssDTO ossResult = ossService.uploadFiled(tempFile);
                frame.setGeneratedVideoUrl(ossResult.getUrl());
                log.info("截帧ID {} 视频生成完成，已上传OSS: {}", frame.getFrameId(), ossResult.getUrl());
            } finally {
                FileUtil.del(tempFile);
            }

            // 7. 更新状态
            frame.setStatus("3"); // 视频生成完成
            frameMapper.updateById(frame);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("截帧ID {} 视频生成失败", frame.getFrameId(), e);
            throw new ServiceException("视频生成失败: " + e.getMessage());
        }
    }

    @Override
    public void undoVideo(Long frameId) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame != null && cn.hutool.core.util.StrUtil.isNotBlank(frame.getPrevVideoUrl())) {
            String temp = frame.getGeneratedVideoUrl();
            frame.setGeneratedVideoUrl(frame.getPrevVideoUrl());
            frame.setPrevVideoUrl(temp);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void deleteTask(Long taskId) {
        // 先删除下属所有的 frame
        frameMapper.delete(new LambdaQueryWrapper<BizVideoReproduceFrame>().eq(BizVideoReproduceFrame::getTaskId, taskId));
        // 再删除主任务记录
        taskMapper.deleteById(taskId);
    }

    @Override
    public void clipVideo(Long frameId, List<java.util.Map<String, Double>> removeRanges) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null || org.apache.commons.lang3.StringUtils.isBlank(frame.getGeneratedVideoUrl())) {
            throw new ServiceException("当前无生视频结果，无法剪贴");
        }

        try {
            // 1. 下载视频到临时文件
            File origVideo = saveToTemp(frame.getGeneratedVideoUrl());
            // 2. 拿到原始长度
            double totalDuration = getVideoDuration(origVideo.getAbsolutePath());
            // 3. 计算提取保留出来的片段
            List<double[]> keepRanges = calculateKeepRanges(totalDuration, removeRanges);
            if (keepRanges.isEmpty()) {
                 cn.hutool.core.io.FileUtil.del(origVideo);
                 throw new ServiceException("剪除所有区间后视频为空！操作不合理");
            }

            // 4. 对每一个保留区间进行精准切片
            List<File> segments = new ArrayList<>();
            String ffmpegPath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe";
            for (int i = 0; i < keepRanges.size(); i++) {
                double[] r = keepRanges.get(i);
                double start = r[0];
                double dur = r[1] - r[0];
                File seg = File.createTempFile("seg_" + i, ".mp4");
                ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath, "-y", "-ss", String.format("%.3f", start), "-t", String.format("%.3f", dur),
                    "-i", origVideo.getAbsolutePath(), "-c:v", "libx264", "-crf", "18", "-c:a", "aac", seg.getAbsolutePath()
                );
                pb.redirectErrorStream(true);
                Process process = pb.start();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {}
                }
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new ServiceException("切片处理环节 FFmpeg 报错，退出码：" + exitCode);
                }
                segments.add(seg);
            }

            // 5. 组合分片文件进行级联
            File listFile = File.createTempFile("list", ".txt");
            StringBuilder sb = new StringBuilder();
            for (File s : segments) {
                sb.append("file '").append(s.getAbsolutePath().replace("\\", "/")).append("'\n");
            }
            cn.hutool.core.io.FileUtil.writeUtf8String(sb.toString(), listFile);

            File finalVideo = File.createTempFile("final_clip", ".mp4");
            ProcessBuilder pbConcat = new ProcessBuilder(
                ffmpegPath, "-y", "-f", "concat", "-safe", "0", "-i", listFile.getAbsolutePath(),
                "-c", "copy", finalVideo.getAbsolutePath()
            );
            pbConcat.redirectErrorStream(true);
            Process processConcat = pbConcat.start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(processConcat.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {}
            }
            processConcat.waitFor();

            // 6. 上传回 Oss 并覆盖回落记录
            OssDTO oss = ossService.uploadFiled(finalVideo);
            frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
            frame.setGeneratedVideoUrl(oss.getUrl());
            frameMapper.updateById(frame);

            // 安全清除文件
            cn.hutool.core.io.FileUtil.del(origVideo);
            for (File s : segments) { cn.hutool.core.io.FileUtil.del(s); }
            cn.hutool.core.io.FileUtil.del(listFile);
            cn.hutool.core.io.FileUtil.del(finalVideo);

        } catch (Exception e) {
            log.error("剪贴失败", e);
            throw new ServiceException("剪贴遇到技术异常: " + e.getMessage());
        }
    }


    @Override
    @Async
    public void mergeVideos(Long taskId) {
        log.info("开始合成全片视频, taskId: {}", taskId);
        BizVideoReproduceTask task = taskMapper.selectById(taskId);
        if (task == null) return;

        List<BizVideoReproduceFrame> frames = getFrames(taskId);
        List<File> videoFiles = new ArrayList<>();

        try {
            // 1. 下载所有已生成的视频片段
            for (BizVideoReproduceFrame frame : frames) {
                if (cn.hutool.core.util.StrUtil.isNotBlank(frame.getGeneratedVideoUrl())) {
                    log.info("正在下载视频片段: {}", frame.getGeneratedVideoUrl());
                    videoFiles.add(saveToTemp(frame.getGeneratedVideoUrl()));
                }
            }

            if (videoFiles.isEmpty()) {
                throw new ServiceException("没有可合成的视频片段");
            }

            // 2. 创建 FFmpeg concat 列表
            File listFile = File.createTempFile("merge_list_", ".txt");
            StringBuilder sb = new StringBuilder();
            for (File f : videoFiles) {
                // FFmpeg concat demuxer 路径需要处理特殊字符或使用相对路径，这里直接用绝对路径且处理斜杠
                sb.append("file '").append(f.getAbsolutePath().replace("\\", "/")).append("'\n");
            }
            FileUtil.writeUtf8String(sb.toString(), listFile);

            // 3. 执行合并
            File outputFile = File.createTempFile("combined_", ".mp4");
            String ffmpegPath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe";

            // 使用 concat demuxer (-f concat) 不需要重新解码，速度极快
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-f", "concat", "-safe", "0", "-i", listFile.getAbsolutePath(),
                "-c", "copy", outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            // 消费输出流，防止进程挂起
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("ffmpeg merge: {}", line);
                }
            }
            process.waitFor();

            if (!outputFile.exists() || outputFile.length() == 0) {
                throw new RuntimeException("FFmpeg 合成产物为空");
            }

            // 4. 上传到 OSS
            log.info("视频合并完成，正在上传到 OSS...");
            OssDTO oss = ossService.uploadFiled(outputFile);

            // 5. 更新任务状态
            task.setCombinedVideoUrl(oss.getUrl());
            taskMapper.updateById(task);
            log.info("全片合成成功: {}", oss.getUrl());

            // 6. 清理临时文件
            FileUtil.del(listFile);
            FileUtil.del(outputFile);
            for (File f : videoFiles) {
                FileUtil.del(f);
            }

        } catch (Exception e) {
            log.error("视频合成失败, taskId: {}", taskId, e);
            updateTaskStatus(taskId, "9", "视频合成失败: " + e.getMessage());
            // 清理已下回的内容
            for (File f : videoFiles) { FileUtil.del(f); }
        }
    }

    private double getVideoDuration(String path) throws Exception {
        String ffprobePath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffprobe.exe";
        ProcessBuilder pb = new ProcessBuilder(
            ffprobePath, "-v", "error", "-show_entries", "format=duration",
            "-of", "default=noprint_wrappers=1:nokey=1", path
        );
        Process process = pb.start();
        String out = cn.hutool.core.io.IoUtil.readUtf8(process.getInputStream()).trim();
        process.waitFor();
        return Double.parseDouble(out);
    }

    private List<double[]> calculateKeepRanges(double totalDur, List<java.util.Map<String, Double>> removeRanges) {
        List<double[]> removes = new ArrayList<>();
        if (removeRanges != null) {
            for (java.util.Map<String, Double> map : removeRanges) {
                if (map.containsKey("start") && map.containsKey("end")) {
                    double s = Math.max(0, map.get("start"));
                    double e = Math.min(totalDur, map.get("end"));
                    if (e > s) removes.add(new double[]{s, e});
                }
            }
        }
        removes.sort(java.util.Comparator.comparingDouble(a -> a[0]));
        List<double[]> mergedRemoves = new ArrayList<>();
        for (double[] r : removes) {
            if (mergedRemoves.isEmpty()) {
                mergedRemoves.add(r);
            } else {
                double[] last = mergedRemoves.get(mergedRemoves.size() - 1);
                if (r[0] <= last[1]) {
                    last[1] = Math.max(last[1], r[1]);
                } else {
                    mergedRemoves.add(r);
                }
            }
        }
        List<double[]> keeps = new ArrayList<>();
        double curr = 0;
        for (double[] r : mergedRemoves) {
            if (r[0] > curr) keeps.add(new double[]{curr, r[0]});
            curr = Math.max(curr, r[1]);
        }
        if (curr < totalDur) keeps.add(new double[]{curr, totalDur});
        return keeps;
    }

    @Override
    public void updateFramePrompts(Long frameId, String promptEn, String promptZh) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame != null) {
            frame.setI2vPromptEn(promptEn);
            frame.setI2vPromptZh(promptZh);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void recaptureFrame(Long frameId, Double timestamp) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null) {
            throw new ServiceException("截帧记录不存在");
        }
        BizVideoReproduceTask task = taskMapper.selectById(frame.getTaskId());
        if (task == null || cn.hutool.core.util.StrUtil.isBlank(task.getOriginalVideoUrl())) {
            throw new ServiceException("任务关联的原始视频不存在");
        }

        try {
            updateTaskStatus(task.getTaskId(), "2", "正在手动重新截帧...");
            File tempVideo = saveToTemp(task.getOriginalVideoUrl());
            try {
                // 截帧逻辑
                String framePath = extractFrame(tempVideo.getAbsolutePath(), timestamp);
                File frameFile = new File(framePath);

                // 上传原截帧到OSS
                OssDTO frameOss = ossService.uploadFiled(frameFile);

                // 更新记录
                frame.setTimestampSec(String.valueOf(timestamp));
                frame.setOriginalImageUrl(frameOss.getUrl());
                // 如果已经洗过图了，建议清除洗图状态或标记失效？
                // 这里暂不清除，让用户自己决定是否重洗，但更新原图是核心。
                frameMapper.updateById(frame);

                FileUtil.del(frameFile);
            } finally {
                FileUtil.del(tempVideo);
            }
        } catch (Exception e) {
            log.error("手动截帧失败", e);
            throw new ServiceException("手动截帧失败: " + e.getMessage());
        }
    }

    @Override
    public void uploadGeneratedVideo(Long frameId, MultipartFile videoFile) {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new ServiceException("上传视频不能为空");
        }
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null) {
            throw new ServiceException("截帧记录不存在");
        }

        try {
            File tempVideo = saveToTemp(videoFile);
            try {
                OssDTO oss = ossService.uploadFiled(tempVideo);

                // 保存历史以支持撤回
                if (cn.hutool.core.util.StrUtil.isNotBlank(frame.getGeneratedVideoUrl())) {
                    frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
                }

                frame.setGeneratedVideoUrl(oss.getUrl());
                frame.setStatus("3"); // 标记为视频已就绪/完成
                frameMapper.updateById(frame);
            } finally {
                FileUtil.del(tempVideo);
            }
        } catch (Exception e) {
            log.error("手动上传生成视频失败", e);
            throw new ServiceException("视频上传失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadAudio(Long frameId, HttpServletResponse response) {
        BizVideoReproduceFrame frame = frameMapper.selectById(frameId);
        if (frame == null || cn.hutool.core.util.StrUtil.isBlank(frame.getGeneratedVideoUrl())) {
            throw new ServiceException("生成视频不存在，无法下载音频");
        }

        File tempVideo = null;
        File tempAudio = null;
        try {
            tempVideo = saveToTemp(frame.getGeneratedVideoUrl());
            tempAudio = File.createTempFile("audio_" + frameId + "_", ".mp3");

            // 提取音频逻辑：由 FFmpeg 根据扩展名自动选择编码器
            String ffmpegPath = "C:\\Users\\CuiMa\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe";
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-i", tempVideo.getAbsolutePath(),
                "-vn", "-ar", "44100", "-ac", "2", "-ab", "192k", tempAudio.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("ffmpeg audio: {}", line);
                }
            }
            int exitCode = process.waitFor();
            
            // 校验结果
            if (exitCode != 0 || !tempAudio.exists() || tempAudio.length() == 0) {
                log.error("FFmpeg 提取音频失败或结果为空. exitCode: {}, path: {}", exitCode, tempAudio.getAbsolutePath());
                throw new RuntimeException("视频中未检测到可提取的音轨或提取失败");
            }

            // 设置响应头并写回流
            response.reset(); // 清除可能残留的 header
            response.setContentType("audio/mpeg");
            response.setHeader("Content-Disposition", "attachment; filename=\"audio_" + frameId + ".mp3\"");
            response.setContentLength((int) tempAudio.length());
            
            // 使用 Hutool 工具类进行高效流拷贝
            try (java.io.InputStream is = new java.io.FileInputStream(tempAudio);
                 java.io.OutputStream os = response.getOutputStream()) {
                cn.hutool.core.io.IoUtil.copy(is, os);
                os.flush();
            }

        } catch (Exception e) {
            log.error("下载音频失败", e);
            // 注意：如果流已经开始写入，抛出异常可能无法正常返回 JSON
            if (!response.isCommitted()) {
                throw new ServiceException("音频提取失败: " + e.getMessage());
            }
        } finally {
            cn.hutool.core.io.FileUtil.del(tempVideo);
            cn.hutool.core.io.FileUtil.del(tempAudio);
        }
    }
}
