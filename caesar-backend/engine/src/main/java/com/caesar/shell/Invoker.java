package com.caesar.shell;

import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;

public class Invoker {
    
    private Command command;

    public Invoker(Command command){
        this.command = command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public ExecutionResult executeCommand() {
        return command.execute();
    }

    public ExecutionResult cancelCommand(Task task) {
        return command.cancel(task);
    }
    
}