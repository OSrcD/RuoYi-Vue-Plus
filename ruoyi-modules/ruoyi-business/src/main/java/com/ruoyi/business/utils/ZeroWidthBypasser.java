package com.ruoyi.business.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ZeroWidthBypasser {
    // 定义零宽字符池：零宽空格、零宽不连字符、零宽连字符、零宽非换行空格
    private static final String[] ZW_POOL = {"\u200B", "\u200C", "\u200D"};
    private static final Random RANDOM = new Random();

    // 丰富的小红书风格表情池（包含你提供的和常用的）
    private static final String[] EMOJI_POOL = {
        "🀄️", "👌", "💰", "🏠", "✨", "🫧", "🎈", "🔥"
    };



    /**
     * 极致混淆函数
     * @param input 原始评论文本
     * @return 注入了随机零宽噪声的文本
     */
    public static String obfuscate(String input) {
        if (input == null || input.isEmpty()) return input;
        // 1. 随机选出3个不重复的表情
        String suffix = getRandomEmojis(2);
        String fullText = input + suffix; // 加个空格过渡
        StringBuilder sb = new StringBuilder();
        // 使用 codePoints 迭代，可以安全处理 Emoji 等多字节字符
//        fullText.codePoints().forEach(cp -> {
//            sb.appendCodePoint(cp);
//
//            // 建议概率保持在 0.7-0.8 之间，留一点“正常”空间反而更难写通用过滤算法
//            if (RANDOM.nextFloat() < 0.6) {
//                // 数量 1-3 个即可，5 个太多了容易触发长度预警
//                int count = RANDOM.nextInt(2) + 1;
//                for (int j = 0; j < count; j++) {
//                    sb.append(ZW_POOL[RANDOM.nextInt(ZW_POOL.length)]);
//                }
//            }
//        });
//        return sb.toString();
        return fullText;
    }


    private static String getRandomEmojis(int count) {
        List<String> list = new ArrayList<>(List.of(EMOJI_POOL));
        Collections.shuffle(list); // 打乱顺序
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < count; i++) {
            res.append(list.get(i));
        }
        return res.toString();
    }

    public static void main(String[] args) {
        String original = "无人棋牌室方案好呢🀄️";
        String processed = obfuscate(original);

        System.out.println("原始长度: " + original.length());
        System.out.println("混淆后长度: " + processed.length());
        System.out.println("预览效果（视觉上应一致）: " + processed);

        // 打印十六进制编码，确认零宽字符已注入
        System.out.print("底层编码片段: ");
        for (int i = 0; i < Math.min(processed.length(), 15); i++) {
            System.out.printf("\\u%04x ", (int) processed.charAt(i));
        }
    }
}
