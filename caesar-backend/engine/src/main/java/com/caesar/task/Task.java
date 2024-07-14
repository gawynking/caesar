package com.caesar.task;

import com.caesar.enums.EngineEnum;
import lombok.Data;

import java.util.Map;

@Data
public class Task {

    private EngineEnum engine;
    private String dbLevel;
    private String taskName;
    private String code;
    private Map<String, String> config;

}
