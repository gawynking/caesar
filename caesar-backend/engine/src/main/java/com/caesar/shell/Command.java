package com.caesar.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;

public interface Command {

    ExecutionResult execute();

    ExecutionResult cancel(Task task);

}
