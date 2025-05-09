package com.caesar.mysql.shell;

import com.caesar.enums.EnvironmentEnum;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Command;
import com.caesar.shell.ShellTask;
import com.caesar.text.model.ScriptInfo;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MySQLCommand extends ShellTask implements Command {

    private MySQLReceiver receiver;
    private ScriptInfo scriptInfo;

    public MySQLCommand(ScriptInfo scriptInfo) {
        this.receiver = new MySQLReceiver(this);
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
    public ExecutionResult<ShellTask> execute() {
        this.buildCommand();
        return receiver.runMysqlJob();
    }

    @Override
    public ExecutionResult<ShellTask> cancel() {
        return receiver.cancelMysqlJob();
    }

}
