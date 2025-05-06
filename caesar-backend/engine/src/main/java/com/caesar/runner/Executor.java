package com.caesar.runner;

import com.caesar.engine.EngineManager;
import com.caesar.runner.params.TaskInfo;

public class Executor {

    private static EngineManager engineManager = new EngineManager();

    public static ExecutionResult execute(TaskInfo task){
        ExecutionResult result = engineManager.execute(task);
        return result;
    }

}
