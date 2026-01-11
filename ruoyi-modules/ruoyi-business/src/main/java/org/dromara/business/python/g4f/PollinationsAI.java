package org.dromara.business.python.g4f;

import jep.Interpreter;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SharedInterpreter;


public class PollinationsAI {

//    private static String jepLibraryPath = "C:/Users/CuiMa/anaconda3/envs/gpt4free/Lib/site-packages/jep/jep.dll";
//    private static String pythonHome = "C:/Users/CuiMa/anaconda3/envs/gpt4free";
//    private static String sysPathAppend = "sys.path.append('C:/Code/gpt4free')";

    private static String jepLibraryPath = "/usr/local/lib64/python3.11/site-packages/jep/libjep.so";
    private static String pythonHome = "/usr";
    private static String sysPathAppend = "sys.path.append('/home/gpt4free')";


    static {
        PyConfig pyConfig = new PyConfig();

        MainInterpreter.setJepLibraryPath(jepLibraryPath);
        pyConfig.setPythonHome(pythonHome);

    }

    public static String getCommentByPrompt(String userPrompt) {
        // 2. 核心修复：每次调用都在方法内部创建 Interpreter
        // 这样确保 Interpreter 是属于“当前执行线程”的
        try (Interpreter interpreter = new SharedInterpreter()) {

            // 导入依赖
            interpreter.exec("import sys");
            interpreter.exec(sysPathAppend);
            interpreter.exec("from g4f.test import test11");
            // 调用 Python 函数
            // 使用 invoke 直接传参，避免拼接字符串产生的注入风险或格式问题
            Object javaResult = interpreter.invoke("test11.get_message_by_qwen_qwen_3", userPrompt);
            if (javaResult != null) {
                return javaResult.toString();
            }
            return null;
        } catch (Exception e) {
            // 建议这里记录日志，方便在服务器上排查
            System.err.println("Jep 执行出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}



