package com.caesar.spark.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.runner.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.spark.shell.SparkCommand;
import com.caesar.text.model.ScriptInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class SparkEngine implements Engine {

    private static final Logger logger = Logger.getLogger(SparkEngine.class.getName());

    private String sparkHome;

    private ScriptInfo scriptInfo;

    public SparkEngine() {
        this.sparkHome = (String) ((Map)((Map)EngineConfig.getMap("environment")).get("spark")).get("home");
    }

    @Override
    public ScriptInfo buildCodeScript(TaskInfo task) {
        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.TEXT);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        return engine.buildCodeScript(task);
    }

    @Override
    public ExecutionResult<ShellTask> execute(TaskInfo task) {
        return executeShell(task);
    }


    public ExecutionResult<ShellTask> executeShell(TaskInfo task) {
        this.scriptInfo = buildCodeScript(task);
        this.scriptInfo.setExecuteUser(task.getSystemUser());
        this.scriptInfo.setEnvironment(task.getEnvironment());
        this.scriptInfo.setFullTaskName(task.getDbName() + "." + task.getTaskName());

        try {
            Invoker invoker = new Invoker(new SparkCommand(this.scriptInfo));
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
