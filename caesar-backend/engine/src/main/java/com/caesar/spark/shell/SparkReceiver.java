package com.caesar.spark.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

public class SparkReceiver {

    ExecutionResult<ShellTask> result;

    public ExecutionResult<Task> runSparkQuery(String[] command) {
        TaskManager taskManager = new TaskManager();
        ExecutionResult<Task> result = taskManager.submitTask(command);
        return result;
    }

    public ExecutionResult cancelSparkQuery(String taskId) {
        return new TaskManager().terminateTask(taskId);
    }

}
