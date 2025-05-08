package com.caesar.doris.factory;

import com.caesar.doris.engine.DorisEngine;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryType;

import java.util.Map;

public class DorisEngineFactory extends EngineFactoryType implements EngineFactory {

    public DorisEngineFactory(){
        this.setEngineEnum();
    }

    @Override
    public Engine createEngine(Map<String, String> config) {
        return new DorisEngine();
    }

    @Override
    public EngineEnum getEngineEnum() {
        return engineEnum;
    }

    @Override
    public void setEngineEnum() {
        this.engineEnum = EngineEnum.DORIS;
    }

}
