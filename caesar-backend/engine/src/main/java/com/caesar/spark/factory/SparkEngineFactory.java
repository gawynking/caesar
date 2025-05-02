package com.caesar.spark.factory;

import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryType;
import com.caesar.spark.engine.SparkEngine;

import java.util.Map;

public class SparkEngineFactory extends EngineFactoryType implements EngineFactory {

    public SparkEngineFactory(){
        this.setEngineEnum();
    }

    @Override
    public Engine createEngine(Map<String, String> config) {
        return new SparkEngine();
    }

    @Override
    public EngineEnum getEngineEnum() {
        return engineEnum;
    }

    @Override
    public void setEngineEnum() {
        this.engineEnum = EngineEnum.SPARK;
    }

}
