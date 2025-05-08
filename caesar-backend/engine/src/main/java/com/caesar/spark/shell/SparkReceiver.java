package com.caesar.spark.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

import java.util.concurrent.ExecutionException;

public class SparkReceiver {

    private static TaskManager taskManager = new TaskManager();

    private ShellTask task;

    ExecutionResult<ShellTask> result;


    public SparkReceiver(ShellTask task){
        this.task = task;
    }


    public ExecutionResult<ShellTask> runSparkQuery()  {
        ExecutionResult<ShellTask> result = null;
        try {
            result = taskManager.submitTask(task);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public ExecutionResult cancelSparkQuery() {
        return taskManager.terminateTask(task.getFullTaskName());
    }

}
