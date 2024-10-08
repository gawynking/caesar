package com.caesar.params;

import com.alibaba.fastjson.JSONObject;
import com.caesar.enums.EngineEnum;
import lombok.Data;

import java.util.Map;

@Data
public class TaskInfo {

    private EngineEnum engine;
    private String systemUser;
    private String dbLevel;
    private String taskName;
    private String code;
    private Map<String, String> taskParams;
    private Map<String,Integer> customParamValues;
    private Map<String, String> config;
    private Map<String, String> engineParams;

}
