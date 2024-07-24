package com.caesar.spark.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Command;
import com.caesar.task.Task;

public class SparkCommand implements Command {
    private SparkReceiver receiver;
    private String command = "spark-submit --master";

    public SparkCommand() {
        this.receiver = new SparkReceiver();
    }

    @Override
    public ExecutionResult execute() {
        return receiver.runSparkJob(command);
    }

    @Override
    public ExecutionResult cancel(Task task) {
        return receiver.cancelSparkJob(command);
    }

}
