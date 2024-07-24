package com.caesar.shell;


import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShellTask extends Task {

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

}
