package com.caesar.doris.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.shell.TaskManager;
import com.caesar.task.Task;

import java.util.concurrent.ExecutionException;

public class DorisReceiver {

    private static TaskManager taskManager = new TaskManager();

    ExecutionResult<ShellTask> result;

    private ShellTask shellTask;

    public DorisReceiver(ShellTask shellTask){
        this.shellTask = shellTask;
    }

    public ExecutionResult<ShellTask> runMysqlJob() {

        ExecutionResult<ShellTask> result = null;
        try {
            result = taskManager.submitTask(shellTask);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public ExecutionResult cancelMysqlJob() {
        return new TaskManager().terminateTask(shellTask.getFullTaskName());
    }

}
