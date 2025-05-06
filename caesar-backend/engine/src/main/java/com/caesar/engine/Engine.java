package com.caesar.engine;

import com.caesar.runner.ExecutionResult;
import com.caesar.runner.params.TaskInfo;

public interface Engine {

    String buildCodeScript(String dbLevel,String taskName,String code);

    ExecutionResult execute(TaskInfo task);

}
