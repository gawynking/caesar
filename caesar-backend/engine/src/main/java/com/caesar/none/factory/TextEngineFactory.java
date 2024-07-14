package com.caesar.none.factory;

import com.caesar.constant.Constant;
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
        return new TextEngine(config.get(Constant.CODE_DIR));
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
