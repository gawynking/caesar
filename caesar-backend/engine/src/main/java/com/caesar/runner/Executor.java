package com.caesar.runner;

import com.caesar.engine.EngineManager;
import com.caesar.runner.params.TaskInfo;
import com.caesar.shell.ShellTask;

public class Executor {

    private static EngineManager engineManager = new EngineManager();

    public static ExecutionResult<ShellTask> execute(TaskInfo task){
        ExecutionResult<ShellTask> result = engineManager.execute(task);
        return result;
    }

}
