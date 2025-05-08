package com.caesar.hive.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

import java.util.concurrent.ExecutionException;

public class HiveReceiver {

    private TaskManager taskManager;
    private ShellTask task;
    private ExecutionResult<ShellTask> result;

    public HiveReceiver(ShellTask task){
        this.task = task;
        this.taskManager = new TaskManager();
    }

    public ExecutionResult<ShellTask> runHiveQuery() {
        try {
            result = taskManager.submitTask(task);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public ExecutionResult cancelHiveQuery() {
        return taskManager.terminateTask(task.getFullTaskName());
    }

}
