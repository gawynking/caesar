package com.caesar.shell;


import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;
import com.caesar.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShellTask extends Task {

    protected String systemUser;

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

    @Override
    public ExecutionResult<Task> run() throws Exception {

        String[] commands = this.getCommand();



        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        try {
            Map<String, String> environment = processBuilder.environment();
            Process process = processBuilder.start();
//            long pid = process.pid();
            this.setProcess(process);
            this.setRunning(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            System.out.println("output = " + output);
            System.out.println("errorOutput = " + errorOutput);

            process.waitFor();
            if (process.exitValue() == 0) {
                this.setOutput(output.toString());
            } else {
                this.setOutput("Error executing task: " + errorOutput.toString());
            }

        } catch (Exception e) {
            this.setOutput("Error executing task: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.setRunning(false);
        }
        return new ExecutionResult<>(this);
    }


    protected List<String> getJobPrefix() {
        ArrayList<String> commands = new ArrayList<>();
        String loginUser = getOSLoginUser();
        if (StringUtils.isEmpty(loginUser)) {
            return commands;
        }
        if(StringUtils.isEmpty(systemUser)){
            return commands;
        }
        if(!loginUser.equals(systemUser.toLowerCase())){
            commands.add("sudo");
            commands.add("-s");
            commands.add("-E");
            commands.add("-u");
            commands.add(systemUser);
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


    protected String dosToUnix(String script) {
        return script.replaceAll("\r\n", "\n");
    }

}
