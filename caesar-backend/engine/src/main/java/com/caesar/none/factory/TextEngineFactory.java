package com.caesar.none.factory;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryType;
import com.caesar.none.engine.TextEngine;

import java.util.Map;

public class TextEngineFactory extends EngineFactoryType implements EngineFactory {

    public TextEngineFactory(){
        this.setEngineEnum();
    }

    @Override
    public Engine createEngine(Map<String, String> config) {
        String fileSystem = (String) EngineConfig.getMap("node").get(EngineConstant.FILE_SYSTEM);
        String codeDir = (String) EngineConfig.getMap("node").get(EngineConstant.CODE_DIR);
        return new TextEngine(fileSystem,codeDir);
    }

    @Override
    public EngineEnum getEngineEnum() {
        return engineEnum;
    }

    @Override
    public void setEngineEnum() {
        engineEnum = EngineEnum.NONE;
    }

}
