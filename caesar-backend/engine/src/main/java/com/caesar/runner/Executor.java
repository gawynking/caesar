package com.caesar.runner;

import com.caesar.engine.EngineManager;
import com.caesar.params.TaskInfo;

public class Executor {

    public static ExecutionResult execute(TaskInfo task){
        EngineManager engineManager = new EngineManager();
        ExecutionResult result = engineManager.execute(task);
        return result;
    }

}
