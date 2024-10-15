package com.caesar.hive.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.hive.shell.HiveCommand;
import com.caesar.mysql.shell.MySQLCommand;
import com.caesar.none.factory.TextEngineFactory;
import com.caesar.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.spark.engine.SparkEngine;
import com.caesar.util.JdbcUrlParserUtils;
import com.caesar.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;


public class HiveEngine extends ShellTask implements Engine {


    public HiveEngine() {
    }

    @Override
    public String buildCodeScript(String dbLevel, String taskName, String code, Boolean isTmp) {
        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.NONE);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        return engine.buildCodeScript(dbLevel,taskName,code,true);
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {
        return executeShell(taskInfo);
    }


    public ExecutionResult executeShell(TaskInfo taskInfo) {
        return new SparkEngine().execute(taskInfo);
    }

}
