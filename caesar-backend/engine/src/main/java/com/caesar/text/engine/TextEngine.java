package com.caesar.text.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.params.TaskInfo;
import com.caesar.text.model.ScriptInfo;
import com.caesar.util.FileDistributorUtils;
import com.caesar.util.FileUtils;
import com.caesar.util.ShellTemplateProcessorUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class TextEngine implements Engine {

    private static final Logger logger = Logger.getLogger(TextEngine.class.getName());

    private List<String> schedulerCluster;
    private String fileSystem;
    private String codeDir;
    private String SEP = File.separator;

    public TextEngine(String fileSystem, String dir, List<String> schedulerCluster){
        this.fileSystem = fileSystem;
        this.schedulerCluster = schedulerCluster;
        if(!dir.endsWith(SEP)){
            this.codeDir = dir + SEP;
        }else {
            this.codeDir = dir;
        }
    }


    @Override
    public ScriptInfo buildCodeScript(TaskInfo task) {
        /**
         * 项目路径设计类似如下风格: code-dir 指定绝对路径
         *  - dw-project/
         *      - sql/
         *          - ods/
         *          - dim/
         *          - dwd/
         */
        String dbLevel = task.getDbLevel();
        String taskName = task.getTaskName();
        String code = task.getCode();
        EngineEnum engine = task.getEngine();
        Map<String, String> customParamValues = task.getCustomParamValues();
        Map<String, String> taskInputParams = task.getTaskInputParams();

        ScriptInfo scriptInfo = new ScriptInfo();
        scriptInfo.setSchedulerCluster(this.schedulerCluster);
        if("file".equals(this.fileSystem)){
            String sqlBasePath = this.codeDir + "sql" + SEP + dbLevel;
            String testSqlFile = sqlBasePath + SEP + taskName + "__test.sql";
            String prodSqlFile = sqlBasePath + SEP + taskName + ".sql";

            FileUtils.createDirectoryIfNotExists(sqlBasePath);
            FileUtils.createFile(testSqlFile);
            FileUtils.createFile(prodSqlFile);

            // 更新临时脚本文件，带${xxx}或${hivevar:xxx}参数SQL脚本
            FileUtils.writeToFile(testSqlFile, code.replaceAll(dbLevel+"."+taskName,dbLevel+"_test."+taskName));
            FileUtils.writeToFile(prodSqlFile, code);

            FileDistributorUtils.distributeFile(testSqlFile,this.schedulerCluster,testSqlFile);
            FileDistributorUtils.distributeFile(prodSqlFile,this.schedulerCluster,prodSqlFile);

            scriptInfo.setTestSqlFile(testSqlFile);
            scriptInfo.setProdSqlFile(prodSqlFile);

            switch (engine){
                case HIVE:
                    String hiveShellTemplate = EngineConfig.getString("HIVE_SHELL");
//                    logger.info(String.format("Hive Shell 模板: \n %s",hiveScriptTemplate));

                    // 测试脚本
                    Map<String, String> testHiveScriptParams = new HashMap<>();
                    StringBuffer testHiveCustomParamsDefineBuffer = new StringBuffer();
                    StringBuffer testHiveParamsBuffer = new StringBuffer();

                    for(String key :customParamValues.keySet()){
                        testHiveCustomParamsDefineBuffer
                                .append(key)
                                .append("=")
                                .append(taskInputParams.get(key))
                                .append("\n");

                        testHiveParamsBuffer
                                .append("--hivevar")
                                .append(" ")
                                .append(key)
                                .append("=")
                                .append(taskInputParams.get(key))
                                .append(" ");
                    }
                    testHiveScriptParams.put("customParamsDefine",testHiveCustomParamsDefineBuffer.toString());
                    testHiveScriptParams.put("hiveParams",testHiveParamsBuffer.toString());
                    testHiveScriptParams.put("sqlFile",testSqlFile);
                    String testHiveScript = ShellTemplateProcessorUtils.processTemplate(hiveShellTemplate, testHiveScriptParams);
                    scriptInfo.setTestScript(testHiveScript);

                    // *************************************************************************************************
                    // 生产调度脚本
                    Map<String, String> prodHiveScriptParams = new HashMap<>();
                    StringBuffer prodHiveCustomParamsDefineBuffer = new StringBuffer();
                    StringBuffer prodHiveParamsBuffer = new StringBuffer();

                    for(String key :customParamValues.keySet()){
                        prodHiveCustomParamsDefineBuffer
                                .append(key)
                                .append("=")
                                .append(customParamValues.get(key))
                                .append("\n");

                        prodHiveParamsBuffer
                                .append("--hivevar")
                                .append(" ")
                                .append(key)
                                .append("=")
                                .append(customParamValues.get(key))
                                .append(" ");
                    }
                    prodHiveScriptParams.put("customParamsDefine",prodHiveCustomParamsDefineBuffer.toString());
                    prodHiveScriptParams.put("hiveParams",prodHiveParamsBuffer.toString());
                    prodHiveScriptParams.put("sqlFile",prodSqlFile);
                    String prodHiveScript = ShellTemplateProcessorUtils.processTemplate(hiveShellTemplate, prodHiveScriptParams);
                    scriptInfo.setProdScript(prodHiveScript);
                    break;
                case SPARK:
                    String sparkShell = EngineConfig.getString("SPARK_SHELL");
                    break;
                case DORIS:
                case MYSQL:
                    Map<String ,String> connectionConfig = null;
                    String mysqlShellTemplate = null;
                    if(engine == EngineEnum.DORIS){
                        mysqlShellTemplate = EngineConfig.getString("DORIS_SHELL");
                        connectionConfig = (Map)EngineConfig.getMap("environment").get("doris");
                    }else{
                        mysqlShellTemplate = EngineConfig.getString("MYSQL_SHELL");
                        connectionConfig = (Map)EngineConfig.getMap("environment").get("mysql");
                    }

                    Map<String, String> prodMysqlScriptParams = new HashMap<>();
                    StringBuffer prodMysqlConnectionBuffer = new StringBuffer();

                    prodMysqlConnectionBuffer
                            .append("-u")
                            .append(connectionConfig.get("username"))
                            .append(" ")
                            .append("-p")
                            .append(connectionConfig.get("password"))
                            .append(" ")
                            .append("-h")
                            .append(connectionConfig.get("hostname"))
                            .append(" ")
                            .append("-P")
                            .append(connectionConfig.get("port"));

                    prodMysqlScriptParams.put("connection",prodMysqlConnectionBuffer.toString());
                    prodMysqlScriptParams.put("sqlFile",prodSqlFile);

                    String prodMysqlScript = ShellTemplateProcessorUtils.processTemplate(mysqlShellTemplate, prodMysqlScriptParams);
                    scriptInfo.setProdScript(prodMysqlScript);

                    prodMysqlScriptParams.put("sqlFile",testSqlFile);
                    String testMysqlScript = ShellTemplateProcessorUtils.processTemplate(mysqlShellTemplate, prodMysqlScriptParams);
                    scriptInfo.setTestScript(testMysqlScript);
                    break;
                default:
                    throw new RuntimeException(String.format("发现不支持引擎%s",engine.getTag()));
            }

        }else if("hdfs".equals(this.fileSystem)){

        }

        logger.info("");
        logger.info(String.format(
                "本次任务 %s 测试脚本路径: %s \n 生产脚本如下: \n%s ",
                dbLevel+"."+taskName,
                scriptInfo.getTestSqlFile(),
                scriptInfo.getTestScript()
        ));
        logger.info("");
        logger.info(String.format(
                "本次任务 %s 生产脚本路径: %s \n 生产脚本如下: \n%s ",
                dbLevel+"."+taskName,
                scriptInfo.getProdSqlFile(),
                scriptInfo.getProdScript()
            ));
        logger.info("");

        return scriptInfo;
    }

    @Override
    public ExecutionResult execute(TaskInfo task) {

        String scriptPath = this.codeDir + task.getDbLevel() + "/" + task.getTaskName() + ".sql";
        File scriptFile = new File(scriptPath);

        try {
            if (scriptFile.exists()) {
                new FileWriter(scriptFile, false).close();
            } else {
                Files.createDirectories(Paths.get(scriptFile.getParent()));
                scriptFile.createNewFile();
            }

            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(task.getCode());
            }

            return new ExecutionResult(true, "File written successfully.", scriptPath);
        } catch (IOException e) {
            e.printStackTrace();
            return new ExecutionResult(false, "Error writing file: " + e.getMessage());
        }

    }

}
