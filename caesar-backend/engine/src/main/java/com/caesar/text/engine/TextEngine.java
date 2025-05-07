package com.caesar.text.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.enums.EnvironmentEnum;
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
         *      - bin/
         *          - ods/
         *          - dim/
         *          - dwd/
         *      - sql/
         *          - ods/
         *          - dim/
         *          - dwd/
         */
        String dbLevel = task.getDbLevel();
        String taskName = task.getTaskName();
        String code = task.getCode();
        EngineEnum engine = task.getEngine();
        EnvironmentEnum environment = task.getEnvironment();
        Map<String, String> customParamValues = task.getCustomParamValues();
        Map<String, String> taskInputParams = task.getTaskInputParams();

        ScriptInfo scriptInfo = new ScriptInfo();
        scriptInfo.setSchedulerCluster(this.schedulerCluster);
        if("file".equals(this.fileSystem)){
            // Shell File
            String shellBasePath = this.codeDir + "bin" + SEP + dbLevel;
            String testShellFile = shellBasePath + SEP + taskName + "__test.sh";
            String prodShellFile = shellBasePath + SEP + taskName + ".sh";

            FileUtils.createDirectoryIfNotExists(shellBasePath);
            FileUtils.createFile(testShellFile);
            FileUtils.createFile(prodShellFile);

            scriptInfo.setTestScriptFile(testShellFile);
            scriptInfo.setProdScriptFile(prodShellFile);

            // SQL File
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

                    if(environment == EnvironmentEnum.TEST) { // 测试脚本
                        Map<String, String> testHiveScriptParams = new HashMap<>();
                        StringBuffer testHiveCustomParamsDefineBuffer = new StringBuffer();
                        StringBuffer testHiveParamsBuffer = new StringBuffer();

                        for (String key : customParamValues.keySet()) {
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
                                    .append("'")
                                    .append(taskInputParams.get(key))
                                    .append("'")
                                    .append(" ");
                        }
                        testHiveScriptParams.put("customParamsDefine", testHiveCustomParamsDefineBuffer.toString());
                        testHiveScriptParams.put("hiveParams", testHiveParamsBuffer.toString());
                        testHiveScriptParams.put("sqlFile", testSqlFile);
                        String testHiveScript = ShellTemplateProcessorUtils.processTemplate(hiveShellTemplate, testHiveScriptParams);
                        scriptInfo.setTestScript(testHiveScript);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(testShellFile, testHiveScript);
                        FileDistributorUtils.distributeFile(testShellFile,this.schedulerCluster,testShellFile);
                    } else if(environment == EnvironmentEnum.PRODUCTION) { // 生产脚本
                        Map<String, String> prodHiveScriptParams = new HashMap<>();
                        StringBuffer prodHiveCustomParamsDefineBuffer = new StringBuffer();
                        StringBuffer prodHiveParamsBuffer = new StringBuffer();

                        for (String key : customParamValues.keySet()) {
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
                                    .append("'")
                                    .append(customParamValues.get(key))
                                    .append("'")
                                    .append(" ");
                        }
                        prodHiveScriptParams.put("customParamsDefine", prodHiveCustomParamsDefineBuffer.toString());
                        prodHiveScriptParams.put("hiveParams", prodHiveParamsBuffer.toString());
                        prodHiveScriptParams.put("sqlFile", prodSqlFile);
                        String prodHiveScript = ShellTemplateProcessorUtils.processTemplate(hiveShellTemplate, prodHiveScriptParams);
                        scriptInfo.setProdScript(prodHiveScript);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(prodShellFile, prodHiveScript);
                        FileDistributorUtils.distributeFile(prodShellFile,this.schedulerCluster,prodShellFile);
                    }
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

                    if(environment == EnvironmentEnum.TEST){
                        Map<String, String> testMysqlScriptParams = new HashMap<>();
                        StringBuffer testMysqlConnectionBuffer = new StringBuffer();

                        testMysqlConnectionBuffer
                                .append("-u")
                                .append(connectionConfig.get("username-test"))
                                .append(" ")
                                .append("-p")
                                .append("'")
                                .append(connectionConfig.get("password-test"))
                                .append("'")
                                .append(" ")
                                .append("-h")
                                .append(connectionConfig.get("hostname-test"))
                                .append(" ")
                                .append("-P")
                                .append(connectionConfig.get("port-test"));

                        testMysqlScriptParams.put("connection", testMysqlConnectionBuffer.toString());
                        testMysqlScriptParams.put("sqlFile", testSqlFile);

                        String testMysqlScript = ShellTemplateProcessorUtils.processTemplate(mysqlShellTemplate, testMysqlScriptParams);
                        scriptInfo.setTestScript(testMysqlScript);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(testShellFile, testMysqlScript);
                        FileDistributorUtils.distributeFile(testShellFile,this.schedulerCluster,testShellFile);
                    }else if(environment == EnvironmentEnum.PRODUCTION) {
                        Map<String, String> prodMysqlScriptParams = new HashMap<>();
                        StringBuffer prodMysqlConnectionBuffer = new StringBuffer();

                        prodMysqlConnectionBuffer
                                .append("-u")
                                .append(connectionConfig.get("username"))
                                .append(" ")
                                .append("-p")
                                .append("'")
                                .append(connectionConfig.get("password"))
                                .append("'")
                                .append(" ")
                                .append("-h")
                                .append(connectionConfig.get("hostname"))
                                .append(" ")
                                .append("-P")
                                .append(connectionConfig.get("port"));

                        prodMysqlScriptParams.put("connection", prodMysqlConnectionBuffer.toString());
                        prodMysqlScriptParams.put("sqlFile", prodSqlFile);

                        String prodMysqlScript = ShellTemplateProcessorUtils.processTemplate(mysqlShellTemplate, prodMysqlScriptParams);
                        scriptInfo.setProdScript(prodMysqlScript);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(prodShellFile, prodMysqlScript);
                        FileDistributorUtils.distributeFile(prodShellFile,this.schedulerCluster,prodShellFile);
                    }
                    break;
                default:
                    throw new RuntimeException(String.format("发现不支持引擎%s",engine.getTag()));
            }

        }else if("hdfs".equals(this.fileSystem)){
        }

        logger.info("");
        if(environment == EnvironmentEnum.TEST) {
            logger.info(String.format(
                    "本次任务 %s 测试SQL脚本路径: %s\n 测试Shell脚本路径: %s\n 测试Shell脚本如下: \n%s ",
                    dbLevel + "." + taskName,
                    scriptInfo.getTestSqlFile(),
                    scriptInfo.getTestScriptFile(),
                    scriptInfo.getTestScript()
            ));
        }else if(environment == EnvironmentEnum.PRODUCTION) {
            logger.info(String.format(
                    "本次任务 %s 生产SQL脚本路径: %s\n 生产Shell脚本路径: %s\n 生产Shell脚本如下: \n%s ",
                    dbLevel + "." + taskName,
                    scriptInfo.getProdSqlFile(),
                    scriptInfo.getProdScriptFile(),
                    scriptInfo.getProdScript()
            ));
        }
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
