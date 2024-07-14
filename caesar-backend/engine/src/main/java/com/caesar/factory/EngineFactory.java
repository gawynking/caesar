package com.caesar.factory;

import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;

import java.util.Map;


public interface EngineFactory {

    Engine createEngine(Map<String, String> config);

    EngineEnum getEngineEnum();

}
