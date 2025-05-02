package com.caesar.hive.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

public class HiveReceiver {

    ExecutionResult<ShellTask> result;

    public ExecutionResult<Task> runHiveQuery(String[] command) {
        TaskManager taskManager = new TaskManager();
        ExecutionResult<Task> result = taskManager.submitTask(command);
        return result;
    }

    public ExecutionResult cancelHiveQuery(String taskId) {
        return new TaskManager().terminateTask(taskId);
    }

}
