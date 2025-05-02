package com.caesar.hive.engine;

import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.ShellTask;
import com.caesar.spark.engine.SparkEngine;
import java.util.HashMap;


public class HiveEngine extends ShellTask implements Engine {


    public HiveEngine() {
    }

    @Override
    public String buildCodeScript(String dbLevel, String taskName, String code) {
        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.NONE);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        return engine.buildCodeScript(dbLevel,taskName,code);
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {
        return executeShell(taskInfo);
    }


    public ExecutionResult executeShell(TaskInfo taskInfo) {
        return new SparkEngine().execute(taskInfo);
    }

}
