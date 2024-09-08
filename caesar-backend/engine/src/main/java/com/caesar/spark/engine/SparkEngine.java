package com.caesar.spark.engine;

import com.caesar.engine.Engine;
import com.caesar.hive.shell.HiveCommand;
import com.caesar.none.factory.TextEngineFactory;
import com.caesar.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;

import java.util.List;


public class SparkEngine extends ShellTask implements Engine {


    public SparkEngine() {
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

        commands.add("spark-sql");
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
