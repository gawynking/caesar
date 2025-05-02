package com.caesar.spark.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

public class SparkReceiver {

    private static TaskManager taskManager = new TaskManager();

    ExecutionResult<ShellTask> result;

    public ExecutionResult<Task> runSparkQuery(String[] command) {
        ExecutionResult<Task> result = taskManager.submitTask(command);
        return result;
    }

    public ExecutionResult cancelSparkQuery(String taskId) {
        return taskManager.terminateTask(taskId);
    }

}
