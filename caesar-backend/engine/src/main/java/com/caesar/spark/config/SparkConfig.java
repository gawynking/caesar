package com.caesar.spark.config;

import java.util.HashMap;
import java.util.Map;

public class SparkConfig {

    private static Map<String,String> coreConf;
    private static Map<String,String> appConf;
    private static Map<String,String> sqlConf;

    static {
        coreConf = new HashMap<>();
        coreConf.put("driver-memory","4g");
        coreConf.put("driver-cores","1");
        coreConf.put("executor-memory","4g");
        coreConf.put("executor-cores","2");
        coreConf.put("num-executors","4");
    }


    public static Map<String,String> getCoreConf(){
        return coreConf;
    }
}
