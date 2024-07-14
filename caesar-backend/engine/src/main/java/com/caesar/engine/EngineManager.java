package com.caesar.engine;

import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;

import java.util.HashMap;
import java.util.Map;


public class EngineManager {

    private EngineFactoryRegistry registry;


    public EngineManager() {
        this.registry = new EngineFactoryRegistry();
    }



    public ExecutionResult execute(Task task) {
        EngineFactory factory = registry.getEngineFactory(task.getEngine());
        Engine engine = factory.createEngine(task.getConfig());
        if (engine == null) {
            throw new IllegalArgumentException("No engine found for type: " + task.getEngine().getEngine());
        }
        return engine.execute(task);
    }

}
