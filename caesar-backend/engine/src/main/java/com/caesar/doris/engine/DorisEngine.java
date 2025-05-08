package com.caesar.doris.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.doris.shell.DorisCommand;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.runner.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.text.model.ScriptInfo;

import java.util.HashMap;
import java.util.Map;


public class DorisEngine implements Engine {

    private Map<String,String> connectionConfig;

    private String jdbcDriver;
    private String jdbcUrl;
    private String username;
    private String password;

    private ScriptInfo scriptInfo;

    public DorisEngine() {
        this.connectionConfig = (Map) EngineConfig.getMap("environment").get("mysql");
    }

    @Override
    public ScriptInfo buildCodeScript(TaskInfo task) {
        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.TEXT);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        return engine.buildCodeScript(task);
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {
        return executeShell(taskInfo);
    }


    public ExecutionResult executeShell(TaskInfo task) {
        this.scriptInfo = buildCodeScript(task);
        this.scriptInfo.setExecuteUser(task.getSystemUser());
        this.scriptInfo.setEnvironment(task.getEnvironment());
        this.scriptInfo.setFullTaskName(task.getDbName() + "." + task.getTaskName());

        try {
            Invoker invoker = new Invoker(new DorisCommand(this.scriptInfo));
            ExecutionResult<ShellTask> result = invoker.executeCommand();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            ShellTask shellTask = new ShellTask();
            shellTask.setFullTaskName(this.scriptInfo.getFullTaskName());
            return new ExecutionResult(false, e.getMessage(),shellTask);
        }
    }

}
