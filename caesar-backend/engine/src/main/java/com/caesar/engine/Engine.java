package com.caesar.engine;

import com.caesar.runner.ExecutionResult;
import com.caesar.params.TaskInfo;

public interface Engine {

    ExecutionResult execute(TaskInfo task);

}
