package com.caesar.spark.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.hive.shell.HiveCommand;
import com.caesar.none.factory.TextEngineFactory;
import com.caesar.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.spark.shell.SparkCommand;
import com.caesar.util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;


public class SparkEngine extends ShellTask implements Engine {

    private static final Logger logger = Logger.getLogger(SparkEngine.class.getName());

    public SparkEngine() {
    }

    @Override
    public ExecutionResult execute(TaskInfo taskInfo) {
        return executeShell(taskInfo);
    }


    public ExecutionResult executeShell(TaskInfo taskInfo) {

        /**
         * 项目路径设计类似如下风格: code-dir 指定绝对路径
         *  - dw-project/
         *      - sbin/
         *          - ods/
         *          - dim/
         *          - dwd/
         *      - sql/
         *          - ods/
         *          - dim/
         *          - dwd/
         */
        String dbLevel = taskInfo.getDbLevel();
        String taskName = taskInfo.getTaskName();
        String sparkShell = EngineConfig.getString(EngineConstant.SPARK_SHELL);
        String codeDir = (String) EngineConfig.getMap("none").get(EngineConstant.CODE_DIR);
        String sqlDirPath = codeDir+"/sql/"+dbLevel;
        String shellDirPath = codeDir+"/sbin/"+dbLevel;

        String sqlFilePath = sqlDirPath+"/tmp__"+taskName+".sql";
        String shellFilePath = shellDirPath+"/tmp__"+taskName+".sh";

        FileUtils.createDirectoryIfNotExists(sqlDirPath);
        FileUtils.createDirectoryIfNotExists(shellDirPath);

        FileUtils.createFile(sqlFilePath);
        FileUtils.createFile(shellFilePath);

        FileUtils.writeToFile(sqlFilePath,taskInfo.getCode()); // 更新临时脚本文件


        /**
         * 替换变量
         *   {{ customParamsDefine }}
         *   {{ engineParamsSetter }}
         *   {{ customParamsSetter }}
         *   {{ sqlFile }}
         */
        String sqlFile = sqlFilePath;
        Map<String, Integer> customParamValues = taskInfo.getCustomParamValues();
        StringBuffer customParamsDefine = new StringBuffer();
        StringBuffer customParamsSetter = new StringBuffer();
        if(null != customParamValues && customParamValues.keySet().size() > 0) {
            int i = 1;
            for (String variable : customParamValues.keySet()) {
                variable = variable.trim();
                customParamValues.put(variable, i);
                customParamsDefine
                        .append(variable)
                        .append("=")
                        .append("$")
                        .append(i)
                        .append("\n");
                customParamsSetter
                        .append("--conf spark.sql.param.")
                        .append(variable)
                        .append("=")
                        .append("${")
                        .append(variable)
                        .append("}");
                i++;
            }
        }

        Map<String, String> engineParams = taskInfo.getEngineParams();
        StringBuffer engineParamsSetter = new StringBuffer();
        if(null != engineParams && engineParams.keySet().size() > 0) {
            for (String key : engineParams.keySet()) {
                engineParamsSetter
                        .append("--")
                        .append(key)
                        .append(" ")
                        .append(engineParams.get(key))
                        .append(" ");
            }
        }else{
            engineParamsSetter
                    .append(" --driver-memory 4G ")
                    .append(" --executor-memory 4G ")
                    .append(" --executor-cores 1 ")
                    .append(" --num-executors 2 ");
        }

        sparkShell = sparkShell.replaceAll("[ ]*\\{\\{\\s*customParamsDefine\\s*\\}\\}\\s*", Matcher.quoteReplacement(customParamsDefine.toString()));
        sparkShell = sparkShell.replaceAll("\\{\\{\\s*customParamsSetter\\s*\\}\\}", Matcher.quoteReplacement(customParamsSetter.toString()));
        sparkShell = sparkShell.replaceAll("\\{\\{\\s*engineParamsSetter\\s*\\}\\}", Matcher.quoteReplacement(engineParamsSetter.toString()));
        sparkShell = sparkShell.replaceAll("\\{\\{\\s*sqlFile\\s*\\}\\}", Matcher.quoteReplacement(sqlFile.toString()));

        FileUtils.writeToFile(shellFilePath,sparkShell); // 更新临时脚本文件


        super.systemUser = taskInfo.getSystemUser();
        List<String> commands = super.getJobPrefix();

        commands.add("sh");
        commands.add(shellFilePath);
        for(String variable:customParamValues.keySet()){
            variable = variable.trim();
            commands.add("${"+variable+"}");
        }

        logger.info("Task script \n ------------------------------------------------ " + String.join(" ",commands));
        logger.info("Task Shell script \n ------------------------------------------------ " + sparkShell);
        logger.info("Task SQL script \n ------------------------------------------------ " + taskInfo.getCode());

        try {
            Invoker invoker = new Invoker(new SparkCommand(commands.toArray(new String[0])));
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
