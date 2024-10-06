package com.caesar.config;

import com.caesar.enums.EngineEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ShellMapping {

    private static final Map<String, String> shellMapping = new HashMap<>();


    public static final String SPARK_TEMPLATE = "shell/spark_shell.sh";
    public static final String HIVE_TEMPLATE = "shell/hive_shell.sh";


    static {
        shellMapping.put(ShellMapping.getKey(EngineEnum.SPARK), SPARK_TEMPLATE);
        shellMapping.put(ShellMapping.getKey(EngineEnum.HIVE), HIVE_TEMPLATE);
    }

    public static Map<String, String> getShellMapping(){
        return shellMapping;
    }

    public static String getKey(EngineEnum engine){
        return engine.getEngine().toUpperCase()+"_SHELL";
    }

    public static String getTemplatePath(EngineEnum engine) {
        return shellMapping.get(getKey(engine));
    }

}
