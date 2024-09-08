package com.caesar.mysql.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.mysql.shell.MySQLCommand;
import com.caesar.runner.ExecutionResult;
import com.caesar.params.TaskInfo;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.util.JdbcUrlParserUtils;
import com.caesar.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class MySQLEngine extends ShellTask implements Engine {

    private String jdbcDriver;
    private String jdbcUrl;
    private String username;
    private String password;

    public MySQLEngine(String jdbcDriver, String jdbcUrl, String username, String password) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {

        String priority = EngineConfig.getString(EngineConstant.PRIORITY);
        if("jdbc".equals(priority)){
            return executeJdbc(taskInfo);
        }else {
            return executeShell(taskInfo);
        }

    }


    public ExecutionResult executeJdbc(TaskInfo taskInfo){
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

    public ExecutionResult executeShell(TaskInfo taskInfo) {


        super.systemUser = taskInfo.getSystemUser();
        List<String> commands = super.getJobPrefix();

        JdbcUrlParserUtils.JdbcUrlInfo jdbcUrlInfo = JdbcUrlParserUtils.parseJdbcUrl(this.jdbcUrl);

        commands.add("mysql");
        commands.add("-u"+this.username);
        commands.add("-p"+this.password);
        commands.add("-h"+jdbcUrlInfo.getHostname());
        commands.add("-P"+jdbcUrlInfo.getPort());
        if(StringUtils.isNotEmpty(jdbcUrlInfo.getDatabase())){
            commands.add(jdbcUrlInfo.getDatabase());
        }
        commands.add("-e");
        commands.add(taskInfo.getCode());


//        String[] commands = new String[]{
//                "/opt/homebrew/Cellar/mysql@5.7/5.7.32/bin/mysql",
//                "-u"+this.username,
//                "-p"+this.password,
//                "-h"+jdbcUrlInfo.getHostname(),
//                "-P"+jdbcUrlInfo.getPort(),
//                StringUtils.isNotEmpty(jdbcUrlInfo.getDatabase())?jdbcUrlInfo.getDatabase():null,
//                "-e",
//                taskInfo.getCode()
//        };


        try {
            Invoker invoker = new Invoker(new MySQLCommand(commands.toArray(new String[0])));
            ExecutionResult<ShellTask> result = invoker.executeCommand();
            if (result.isSuccess()) {
                return new ExecutionResult(true, "Task submit execute.");
            } else {
                return new ExecutionResult(false, "Failed to submit task.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ExecutionResult(false, e.getMessage());
        }
    }

}
