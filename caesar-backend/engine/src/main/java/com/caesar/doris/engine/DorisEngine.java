package com.caesar.doris.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.doris.shell.DorisCommand;
import com.caesar.engine.Engine;
import com.caesar.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.util.JdbcUrlParserUtils;
import com.caesar.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class DorisEngine extends ShellTask implements Engine {

    private String jdbcDriver;
    private String jdbcUrl;
    private String username;
    private String password;

    public DorisEngine(String jdbcDriver, String jdbcUrl, String username, String password) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public String buildCodeScript(String dbLevel, String taskName, String code) {
        return null;
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {
        return executeShell(taskInfo);
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

        try {
            Invoker invoker = new Invoker(new DorisCommand(commands.toArray(new String[0])));
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
