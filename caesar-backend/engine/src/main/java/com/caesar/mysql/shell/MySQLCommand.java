package com.caesar.mysql.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Command;
import com.caesar.task.Task;

public class MySQLCommand implements Command {

    private MySQLReceiver receiver;
    private String[] command;

    public MySQLCommand(String[] command) {
        this.receiver = new MySQLReceiver();
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
