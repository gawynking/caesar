package com.caesar.task;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public abstract class Task {

    private String id;
    private String fullTaskName; // 一个taskName同一时刻只能有一个实例运行
    private String[] command;
    private ExecuteStatus status;

    private Process process;
    private String output;
    private String executionLog;


    public abstract ExecutionResult<ShellTask> run() throws Exception;

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

    public Map<String,String> genEnvironment(){
        Map<String, String> envMap = (Map<String, String>) EngineConfig.getMap(EngineConstant.ENVIRONMENT);
        return envMap;
    }



    public static enum ExecuteStatus {
        CREATED,        // 任务已创建
        RUNNING,        // 正在运行
        SUCCESS,        // 成功完成
        FAILED,         // 运行失败
        CANCELLED,      // 被取消
        TIMEOUT,        // 超时
        UNKNOWN;        // 未知状态（用于兜底）

        public boolean isFinished() {
            return this == SUCCESS || this == FAILED || this == CANCELLED || this == TIMEOUT;
        }

        public boolean isRunning() {
            return this == RUNNING;
        }

        public boolean isSuccess() {
            return this == SUCCESS;
        }

        public boolean isFailed() {
            return this == FAILED;
        }
    }


}
