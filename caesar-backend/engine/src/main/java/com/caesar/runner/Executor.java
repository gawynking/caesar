package com.caesar.runner;

import com.caesar.engine.EngineManager;
import com.caesar.task.Task;

public class Executor {

    public static ExecutionResult execute(Task task){
        EngineManager engineManager = new EngineManager();
        ExecutionResult result = engineManager.execute(task);
        return result;
    }

}
