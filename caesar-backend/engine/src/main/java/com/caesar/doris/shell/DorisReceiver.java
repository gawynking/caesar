package com.caesar.doris.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

public class DorisReceiver {

    ExecutionResult<ShellTask> result;

    public ExecutionResult<Task> runMysqlJob(String[] command) {
        TaskManager taskManager = new TaskManager();
        ExecutionResult<Task> result = taskManager.submitTask(command);
        return result;
    }

    public ExecutionResult cancelMysqlJob(String taskId) {
        return new TaskManager().terminateTask(taskId);
    }

}
