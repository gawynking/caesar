package com.caesar.config;

import com.caesar.enums.EngineEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ShellMapping {

    private static final Map<String, String> shellMapping = new HashMap<>();

    public static final String HIVE_SHELL = "shell/hive.sh";
    public static final String SPARK_SHELL = "shell/spark.sh";
    public static final String DORIS_SHELL = "shell/doris.sh";
    public static final String MYSQL_SHELL = "shell/mysql.sh";


    static {
        shellMapping.put(ShellMapping.getKey(EngineEnum.HIVE), HIVE_SHELL);
        shellMapping.put(ShellMapping.getKey(EngineEnum.SPARK), SPARK_SHELL);
        shellMapping.put(ShellMapping.getKey(EngineEnum.DORIS), DORIS_SHELL);
        shellMapping.put(ShellMapping.getKey(EngineEnum.MYSQL), MYSQL_SHELL);
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
