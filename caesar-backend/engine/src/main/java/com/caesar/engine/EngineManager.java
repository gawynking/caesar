package com.caesar.engine;

import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.params.TaskInfo;


public class EngineManager {

    private static EngineFactoryRegistry registry;

    static {
        registry = new EngineFactoryRegistry();
    }

    public EngineManager() {
    }


    public ExecutionResult execute(TaskInfo task) {
        EngineFactory factory = registry.getEngineFactory(task.getEngine());
        Engine engine = factory.createEngine(task.getConfig());
        if (engine == null) {
            throw new IllegalArgumentException("No engine found for type: " + task.getEngine().getEngine());
        }
        return engine.execute(task);
    }

}
