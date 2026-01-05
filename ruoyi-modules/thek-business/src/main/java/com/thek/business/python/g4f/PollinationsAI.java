package com.thek.business.python.g4f;

import jep.Interpreter;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SharedInterpreter;

public class PollinationsAI {


    public static void main(String[] args) {
        MainInterpreter.setJepLibraryPath("C:/Users/CuiMa/anaconda3/envs/gpt4free/Lib/site-packages/jep/jep.dll");
        PyConfig config = new PyConfig();
        config.setPythonHome("C:/Users/CuiMa/anaconda3/envs/gpt4free");
        Interpreter interpreter = new SharedInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("sys.path.append('C:/Code/gpt4free')");
        interpreter.exec("from g4f.test import test11");
        interpreter.exec("result = test11.get_message_by_qwen_qwen_3()");
        String result = interpreter.getValue("result", String.class);
        System.out.println(result);
    }


}



