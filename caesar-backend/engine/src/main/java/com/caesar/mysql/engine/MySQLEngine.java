package com.caesar.mysql.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.enums.EnvironmentEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.hive.shell.HiveCommand;
import com.caesar.mysql.shell.MySQLCommand;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.params.TaskInfo;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.text.model.ScriptInfo;
import com.caesar.util.JdbcUrlParserUtils;
import com.caesar.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MySQLEngine implements Engine {

    private Map<String,String> connectionConfig;
    private String jdbcDriver;
    private String jdbcUrl;
    private String username;
    private String password;

    private ScriptInfo scriptInfo;

    public MySQLEngine() {
        this.connectionConfig = (Map)EngineConfig.getMap("environment").get("mysql");
    }

    @Override
    public ScriptInfo buildCodeScript(TaskInfo task) {
        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.TEXT);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        return engine.buildCodeScript(task);
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {

        String style = connectionConfig.get("style");
        if("jdbc".equals(style)){
            return executeJdbc(taskInfo);
        }else {
            return executeShell(taskInfo);
        }

    }


    public ExecutionResult<ShellTask> executeJdbc(TaskInfo taskInfo){
        EnvironmentEnum environment = taskInfo.getEnvironment();
        switch (environment){
            case TEST:
                break;
            case PRODUCTION:
                break;
            default:
                throw new RuntimeException();
        }

        // 显式加载 MySQL JDBC 驱动
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver.", e);
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
            String[] codes = taskInfo.getCode().split(";");
            for(String code:codes){
                stmt.execute(code);
            }
            return new ExecutionResult(true, "Execution successful.");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ExecutionResult(false, e.getMessage());
        }
    }

    public ExecutionResult<ShellTask> executeShell(TaskInfo task) {
        this.scriptInfo = buildCodeScript(task);
        this.scriptInfo.setExecuteUser(task.getSystemUser());
        this.scriptInfo.setEnvironment(task.getEnvironment());
        this.scriptInfo.setFullTaskName(task.getDbName() + "." + task.getTaskName());

        try {
            Invoker invoker = new Invoker(new MySQLCommand(this.scriptInfo));
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
