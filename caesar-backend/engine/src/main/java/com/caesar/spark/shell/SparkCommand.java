package com.caesar.spark.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Command;
import com.caesar.task.Task;

public class SparkCommand implements Command {
    private SparkReceiver receiver;
    private String[] command;

    public SparkCommand(String[] command) {
        this.receiver = new SparkReceiver();
        this.command = command;
    }

    @Override
    public ExecutionResult execute() {
        return receiver.runSparkQuery(command);
    }

    @Override
    public ExecutionResult cancel(Task task) {
        return receiver.cancelSparkQuery(task.getId());
    }

}