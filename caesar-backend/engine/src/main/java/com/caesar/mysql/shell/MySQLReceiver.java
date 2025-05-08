package com.caesar.mysql.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

import java.util.concurrent.ExecutionException;

public class MySQLReceiver{

    private static TaskManager taskManager = new TaskManager();

    ShellTask task;

    ExecutionResult<ShellTask> result;

    public MySQLReceiver(ShellTask task){
        this.task = task;
    }

    public ExecutionResult<ShellTask> runMysqlJob() {

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

    public ExecutionResult cancelMysqlJob() {
        return new TaskManager().terminateTask(task.getFullTaskName());
    }

}
