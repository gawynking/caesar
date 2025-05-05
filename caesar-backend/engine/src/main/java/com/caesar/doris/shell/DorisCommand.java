package com.caesar.doris.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Command;
import com.caesar.task.Task;

public class DorisCommand implements Command {

    private DorisReceiver receiver;
    private String[] command;

    public DorisCommand(String[] command) {
        this.receiver = new DorisReceiver();
        this.command = command;
    }

    @Override
    public ExecutionResult execute() {
        return receiver.runMysqlJob(command);
    }

    @Override
    public ExecutionResult cancel(Task task) {
        return receiver.cancelMysqlJob(task.getId());
    }

}
