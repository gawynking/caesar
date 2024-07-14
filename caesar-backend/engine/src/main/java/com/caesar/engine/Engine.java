package com.caesar.engine;

import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;

public interface Engine {

    ExecutionResult execute(Task task);

}
