package com.caesar.text.factory;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryType;
import com.caesar.text.engine.TextEngine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TextEngineFactory extends EngineFactoryType implements EngineFactory {

    public TextEngineFactory(){
        this.setEngineEnum();
    }

    @Override
    public Engine createEngine(Map<String, String> config) {
        String fileSystem = (String) EngineConfig.getMap("text").get(EngineConstant.FILE_SYSTEM);
        String codeDir = (String) EngineConfig.getMap("text").get(EngineConstant.CODE_DIR);
        List<String> schedulerCluster = Arrays.asList(((String) EngineConfig.getMap("environment").get(EngineConstant.SCHEDULER_CLUSTER)).split(","));
        return new TextEngine(fileSystem,codeDir,schedulerCluster);
    }

    @Override
    public EngineEnum getEngineEnum() {
        return engineEnum;
    }

    @Override
    public void setEngineEnum() {
        engineEnum = EngineEnum.TEXT;
    }

}
