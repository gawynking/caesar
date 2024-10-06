package com.caesar.params;

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
    private Map<String,Integer> customParamValues;
    private Map<String, String> config;
    private Map<String, String> engineParams;

}
