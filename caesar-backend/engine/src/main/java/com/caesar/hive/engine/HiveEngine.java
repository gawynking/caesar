package com.caesar.hive.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.hive.shell.HiveCommand;
import com.caesar.mysql.shell.MySQLCommand;
import com.caesar.none.factory.TextEngineFactory;
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


public class HiveEngine extends ShellTask implements Engine {


    public HiveEngine() {
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {
        return executeShell(taskInfo);
    }


    public ExecutionResult executeShell(TaskInfo taskInfo) {

        TextEngineFactory textEngineFactory = new TextEngineFactory();
        Engine textEngineFactoryEngine = textEngineFactory.createEngine(taskInfo.getConfig());
        ExecutionResult textExecutionResult = textEngineFactoryEngine.execute(taskInfo);
        String scriptPath = (String) textExecutionResult.getData();

        super.systemUser = taskInfo.getSystemUser();
        List<String> commands = super.getJobPrefix();

        commands.add("hive");
        commands.add("-f");
        commands.add(scriptPath);


        try {
            Invoker invoker = new Invoker(new HiveCommand(commands.toArray(new String[0])));
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
