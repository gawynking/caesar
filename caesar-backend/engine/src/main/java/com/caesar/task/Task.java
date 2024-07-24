package com.caesar.task;

import com.caesar.runner.ExecutionResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public abstract class Task {

    private String id;
    private String[] command;
    private Process process;
    private boolean isRunning;
    private String output;

    public abstract ExecutionResult<Task> run() throws Exception;

    public abstract List<String> getCommandList();

    public static String[] partitionCommandLine(String command) {
        List<String> commands = new ArrayList<>();
        StringBuilder builder = new StringBuilder(command.length());
        int index = 0;
        boolean isApostrophe = false;
        boolean isQuote = false;
        while (index < command.length()) {
            char c = command.charAt(index);
            switch (c) {
                case ' ':
                    if (!isQuote && !isApostrophe) {
                        String arg = builder.toString();
                        builder = new StringBuilder(command.length() - index);
                        if (arg.length() > 0) {
                            commands.add(arg);
                        }
                    } else {
                        builder.append(c);
                    }
                    break;
                case '\'':
                    if (!isQuote) {
                        isApostrophe = !isApostrophe;
                    } else {
                        builder.append(c);
                    }
                    break;
                case '"':
                    if (!isApostrophe) {
                        isQuote = !isQuote;
                    } else {
                        builder.append(c);
                    }
                    break;
                default:
                    builder.append(c);
            }
            index++;
        }
        if (builder.length() > 0) {
            String arg = builder.toString();
            commands.add(arg);
        }
        return commands.toArray(new String[0]);
    }

}
