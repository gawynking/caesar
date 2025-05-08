package com.caesar.shell;


import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;
import com.caesar.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ShellTask extends Task {

    private static final Logger logger = Logger.getLogger(ShellTask.class.getName());

    protected String SEP = File.separator;

    protected String executeUser;

    @Override
    public List<String> getCommandList(){
        ArrayList<String> commandList = new ArrayList<>();
        String[] commands = getCommand();
        for(String command:commands){
            if(null != command && !"".equals(command.replaceAll("\\s",""))){
                commandList.add(command);
            }
        }
        return commandList;
    }

    protected List<String> buildCommand(){
        throw new RuntimeException();
    }

    @Override
    public ExecutionResult<ShellTask> run() throws Exception {

        String[] commands = this.getCommand();
        logger.info(String.format("任务 %s 执行命令: %s",this.getFullTaskName(),String.join(" ",commands)));

        StringBuilder executionLog = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            Map<String, String> environment = processBuilder.environment();

            Process process = processBuilder.start();
            this.setProcess(process);
            this.setStatus(ExecuteStatus.RUNNING);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                logger.info(line);
                output.append(line).append("\n");
                executionLog.append("[STDOUT] ").append(line).append("\n");
            }

            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                logger.warning(line);
                errorOutput.append(line).append("\n");
                executionLog.append("[STDERR] ").append(line).append("\n");
            }

            process.waitFor();

            if (process.exitValue() == 0) {
                this.setStatus(ExecuteStatus.SUCCESS);
                this.setOutput(output.toString());
            } else {
                this.setStatus(ExecuteStatus.FAILED);
                this.setOutput("Error executing task:\n" + errorOutput.toString());
            }

        } catch (Exception e) {
            this.setStatus(ExecuteStatus.FAILED);
            this.setOutput("Error executing task: " + e.getMessage());
            executionLog.append("[EXCEPTION] ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        // 将执行日志一并返回
        this.setExecutionLog(executionLog.toString());
        ExecutionResult<ShellTask> result = new ExecutionResult<>(this);
        return result;
    }


    protected List<String> getJobPrefix() {
        ArrayList<String> commands = new ArrayList<>();
        String loginUser = getOSLoginUser();
        if (StringUtils.isEmpty(loginUser)) {
            return commands;
        }
        if(StringUtils.isEmpty(executeUser)){
            return commands;
        }
        if(!loginUser.equals(executeUser.toLowerCase())){
            commands.add("sudo");
            commands.add("-s");
            commands.add("-E");
            commands.add("-u");
            commands.add(executeUser);
            return commands;
        }
        return commands;
    }


    private String getOSLoginUser(){
        String loginUser = null;
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.indexOf("linux")>0){
            try {
                String command = "whoami";
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                loginUser = reader.readLine();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loginUser;
    }


}
