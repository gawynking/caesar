package com.caesar.spark.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.constant.EngineConstant;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.params.TaskInfo;
import com.caesar.runner.ExecutionResult;
import com.caesar.shell.Invoker;
import com.caesar.shell.ShellTask;
import com.caesar.spark.shell.SparkCommand;
import com.caesar.util.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;


public class SparkEngine extends ShellTask implements Engine {

    private static final Logger logger = Logger.getLogger(SparkEngine.class.getName());

    public SparkEngine() {
    }

    @Override
    public String buildCodeScript(String dbLevel,String taskName,String code) {
        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.NONE);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        return engine.buildCodeScript(dbLevel,taskName,code);
    }

    @Override
    protected String buildShellScript(TaskInfo taskInfo,String sqlFilePath) {
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
        String shellDirPath = codeDir+SEP+"sbin"+SEP+dbLevel;
        String shellFilePath = shellDirPath+SEP+"tmp__"+taskName+".sh";
        FileUtils.createDirectoryIfNotExists(shellDirPath);
        FileUtils.createFile(shellFilePath);

        /**
         * 替换变量
         *   {{ customParamsDefine }}
         *   {{ engineParamsSetter }}
         *   {{ customParamsSetter }}
         *   {{ sqlFile }}
         */
        String sqlFile = sqlFilePath;
        Map<String, String> customParamValues = taskInfo.getCustomParamValues(); // 脚本解析参数
        StringBuffer customParamsDefine = new StringBuffer();
        StringBuffer customParamsSetter = new StringBuffer();
        if(null != customParamValues && customParamValues.keySet().size() > 0) {
            int i = 1;
            for (String variable : customParamValues.keySet()) {
                variable = variable.trim();
                customParamsDefine
                        .append(variable)
                        .append("=")
                        .append("$")
                        .append(i)
                        .append("\n");
                customParamsSetter
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


        return shellFilePath;
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

        String sqlFilePath = buildCodeScript(dbLevel,taskName,taskInfo.getCode());
        String shellFilePath = buildShellScript(taskInfo,sqlFilePath);

        super.systemUser = taskInfo.getSystemUser();
        List<String> commands = super.getJobPrefix();

        commands.add("sh");
        commands.add(shellFilePath);
        // caesar执行，这里要替换成变量对应的具体值
        Map<String, String> customParamValues = taskInfo.getCustomParamValues(); // 脚本解析参数
        Map<String, String> taskParams = taskInfo.getTaskParams(); // 前端设置参数
        for(String variable:customParamValues.keySet()){
            variable = variable.trim();
            if(taskParams.containsKey(variable)){
                commands.add(taskParams.get(variable));
            }else{
                throw new RuntimeException("未知参数异常");
            }
        }

        logger.info("Task script \n ------------------------------------------------ " + String.join(" ",commands));
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
