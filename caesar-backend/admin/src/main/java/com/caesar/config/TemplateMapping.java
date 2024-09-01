package com.caesar.config;

import com.caesar.enums.EngineEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TemplateMapping {

    private static final Map<String, String> templateMapping = new HashMap<>();


    public static final String MYSQL_TEMPLATE = "template/MysqlTemplate.code";
    public static final String DORIS_TEMPLATE = "template/DorisTemplate.code";
    public static final String SPARK_TEMPLATE = "template/SparkTemplate.code";
    public static final String HIVE_TEMPLATE = "template/HiveTemplate.code";


    static {
        templateMapping.put(TemplateMapping.getKey(EngineEnum.MYSQL), MYSQL_TEMPLATE);
        templateMapping.put(TemplateMapping.getKey(EngineEnum.DORIS), DORIS_TEMPLATE);
        templateMapping.put(TemplateMapping.getKey(EngineEnum.SPARK), SPARK_TEMPLATE);
        templateMapping.put(TemplateMapping.getKey(EngineEnum.HIVE), HIVE_TEMPLATE);
    }

    public static String getKey(EngineEnum engine){
        return engine.getEngine().toUpperCase()+"_TEMPLATE";
    }

    public static String getTemplatePath(EngineEnum engine) {
        return templateMapping.get(getKey(engine));
    }

}
