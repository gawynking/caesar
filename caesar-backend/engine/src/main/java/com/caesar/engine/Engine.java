package com.caesar.engine;

import com.caesar.runner.ExecutionResult;
import com.caesar.params.TaskInfo;

public interface Engine {

    String buildCodeScript(String dbLevel,String taskName,String code,Boolean isTmp);

    ExecutionResult execute(TaskInfo task);

}
