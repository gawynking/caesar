package com.caesar.mysql.factory;

import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryType;
import com.caesar.mysql.engine.MySQLEngine;

import java.util.Map;

public class MySQLEngineFactory extends EngineFactoryType implements EngineFactory {

    public MySQLEngineFactory(){
        this.setEngineEnum();
    }

    @Override
    public Engine createEngine(Map<String, String> config) {
        return new MySQLEngine();
    }

    @Override
    public EngineEnum getEngineEnum() {
        return engineEnum;
    }

    @Override
    public void setEngineEnum() {
        this.engineEnum = EngineEnum.MYSQL;
    }

}
