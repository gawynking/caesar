package com.caesar.doris.shell;

import com.caesar.enums.EnvironmentEnum;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Command;
import com.caesar.shell.ShellTask;
import com.caesar.task.Task;
import com.caesar.text.model.ScriptInfo;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DorisCommand extends ShellTask implements Command {

    private DorisReceiver receiver;
    private ScriptInfo scriptInfo;

    public DorisCommand(ScriptInfo scriptInfo) {
        this.receiver = new DorisReceiver(this);
        this.scriptInfo = scriptInfo;
    }

    @Override
    protected List<String> buildCommand() {

        List<String> schedulerCluster = scriptInfo.getSchedulerCluster();

        super.executeUser = scriptInfo.getExecuteUser();
        List<String> commands = super.getJobPrefix();

        commands.add("ssh");
        commands.add(String.format("%s@%s",super.executeUser,schedulerCluster.get(new Random().nextInt(schedulerCluster.size()))));
        commands.add("sh");
        if(scriptInfo.getEnvironment() == EnvironmentEnum.TEST){
            commands.add(scriptInfo.getTestScriptFile());
        }else if(scriptInfo.getEnvironment() == EnvironmentEnum.PRODUCTION){
            commands.add(scriptInfo.getProdScriptFile());
        }

        super.setId(UUID.randomUUID().toString().toLowerCase().replaceAll("-",""));
        super.setCommand(commands.toArray(new String[0]));
        super.setStatus(ExecuteStatus.CREATED);
        super.setFullTaskName(scriptInfo.getFullTaskName());

        return commands;
    }


    @Override
    public ExecutionResult execute() {
        this.buildCommand();
        return receiver.runMysqlJob();
    }

    @Override
    public ExecutionResult cancel() {
        return receiver.cancelMysqlJob();
    }

}
