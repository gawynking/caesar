package com.caesar.doris.factory;

import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryType;
import com.caesar.mysql.engine.MySQLEngine;

import java.util.Map;

public class DorisEngineFactory extends EngineFactoryType implements EngineFactory {

    public DorisEngineFactory(){
        this.setEngineEnum();
    }

    @Override
    public Engine createEngine(Map<String, String> config) {
        String jdbcDriver = config.get(EngineConstant.DRIVER);
        String jdbcUrl = config.get(EngineConstant.URL);
        String username = config.get(EngineConstant.USERNAME);
        String password = config.get(EngineConstant.PASSWORD);
        return new MySQLEngine(jdbcDriver,jdbcUrl,username,password);
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
