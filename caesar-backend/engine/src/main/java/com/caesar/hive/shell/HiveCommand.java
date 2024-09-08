package com.caesar.hive.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Command;
import com.caesar.task.Task;

public class HiveCommand implements Command {
    private HiveReceiver receiver;
    private String[] command;

    public HiveCommand(String[] command) {
        this.receiver = new HiveReceiver();
    }

    @Override
    public ExecutionResult execute() {
        return receiver.runHiveQuery(command);
    }

    @Override
    public ExecutionResult cancel(Task task) {
        return receiver.cancelHiveQuery(task.getId());
    }

}