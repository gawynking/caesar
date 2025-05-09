package com.caesar.text.engine;

import com.caesar.constant.EngineConfig;
import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.enums.EnvironmentEnum;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.params.TaskInfo;
import com.caesar.shell.ShellTask;
import com.caesar.spark.config.SparkConfig;
import com.caesar.text.model.ScriptInfo;
import com.caesar.util.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.apache.logging.log4j.ThreadContext.containsKey;


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
        String taskTag = task.getTaskName().replaceAll(task.getDbName()+"\\.","").replaceAll("\\.","__");
        String dbName = task.getDbName();
        String tableName = task.getTableName();
        String code = task.getCode();
        EngineEnum engine = task.getEngine();
        EnvironmentEnum environment = task.getEnvironment();
        Map<String, String> customParamValues = Optional.ofNullable(task.getCustomParamValues()).orElse(new HashMap<>());
        Map<String, String> taskInputParams = Optional.ofNullable(task.getTaskInputParams()).orElse(new HashMap<>());
        Map<String, String> engineParams = Optional.ofNullable(task.getEngineParams()).orElse(new HashMap<>());

        ScriptInfo scriptInfo = new ScriptInfo();
        scriptInfo.setSchedulerCluster(this.schedulerCluster);
        if("file".equals(this.fileSystem)){
            // Shell File
            String shellBasePath = this.codeDir + "bin" + SEP + dbName;
            String testShellFile = shellBasePath + SEP + taskTag + "___test.sh";
            String prodShellFile = shellBasePath + SEP + taskTag + ".sh";

            FileUtils.createDirectoryIfNotExists(shellBasePath);
            FileUtils.createFile(testShellFile);
            FileUtils.createFile(prodShellFile);

            scriptInfo.setTestScriptFile(testShellFile);
            scriptInfo.setProdScriptFile(prodShellFile);

            // SQL File
            String sqlBasePath = this.codeDir + "sql" + SEP + dbName;
            String testSqlFile = sqlBasePath + SEP + taskTag + "___test.sql";
            String prodSqlFile = sqlBasePath + SEP + taskTag + ".sql";

            FileUtils.createDirectoryIfNotExists(sqlBasePath);
            FileUtils.createFile(testSqlFile);
            FileUtils.createFile(prodSqlFile);

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

                        StringBuffer codePrefixBuffer = new StringBuffer();
                        for(String key: engineParams.keySet()){
                            codePrefixBuffer
                                    .append("set")
                                    .append(" ")
                                    .append(key)
                                    .append("=")
                                    .append(engineParams.get(key))
                                    .append(";")
                                    .append("\n");
                        }
                        codePrefixBuffer.append("\n");

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
                                    .append("\"")
                                    .append("${"+key+"}")
                                    .append("\"")
                                    .append(" ");
                        }
                        testHiveScriptParams.put("customParamsDefine", testHiveCustomParamsDefineBuffer.toString());
                        testHiveScriptParams.put("hiveParams", testHiveParamsBuffer.toString());
                        testHiveScriptParams.put("sqlFile", testSqlFile);
                        String testHiveScript = ShellTemplateProcessorUtils.processTemplate(hiveShellTemplate, testHiveScriptParams);
                        testHiveScript = CodeUtils.dosToUnix(testHiveScript);
                        scriptInfo.setTestScript(testHiveScript);

                        // 生成SQL脚本
                        FileUtils.writeToFile(testSqlFile, CodeUtils.addEngineParamsForHive(code.replaceAll(dbName+"."+tableName,dbName+"_test."+tableName),codePrefixBuffer.toString()));
                        FileDistributorUtils.distributeFile(testSqlFile,this.schedulerCluster,testSqlFile);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(testShellFile, testHiveScript);
                        FileDistributorUtils.distributeFile(testShellFile,this.schedulerCluster,testShellFile);
                    } else if(environment == EnvironmentEnum.PRODUCTION) { // 生产脚本
                        Map<String, String> prodHiveScriptParams = new HashMap<>();
                        StringBuffer prodHiveCustomParamsDefineBuffer = new StringBuffer();
                        StringBuffer prodHiveParamsBuffer = new StringBuffer();

                        StringBuffer codePrefixBuffer = new StringBuffer();
                        for(String key: engineParams.keySet()){
                            codePrefixBuffer
                                    .append("set")
                                    .append(" ")
                                    .append(key)
                                    .append("=")
                                    .append(engineParams.get(key))
                                    .append(";")
                                    .append("\n");
                        }
                        codePrefixBuffer.append("\n");


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
                                    .append("\"")
                                    .append("${"+key+"}")
                                    .append("\"")
                                    .append(" ");
                        }
                        prodHiveScriptParams.put("customParamsDefine", prodHiveCustomParamsDefineBuffer.toString());
                        prodHiveScriptParams.put("hiveParams", prodHiveParamsBuffer.toString());
                        prodHiveScriptParams.put("sqlFile", prodSqlFile);
                        String prodHiveScript = ShellTemplateProcessorUtils.processTemplate(hiveShellTemplate, prodHiveScriptParams);
                        prodHiveScript = CodeUtils.dosToUnix(prodHiveScript);
                        scriptInfo.setProdScript(prodHiveScript);

                        // 生成SQL脚本文件
                        FileUtils.writeToFile(prodSqlFile, CodeUtils.addEngineParamsForHive(code,codePrefixBuffer.toString()));
                        FileDistributorUtils.distributeFile(prodSqlFile,this.schedulerCluster,prodSqlFile);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(prodShellFile, prodHiveScript);
                        FileDistributorUtils.distributeFile(prodShellFile,this.schedulerCluster,prodShellFile);
                    }
                    break;
                case SPARK:
                    String sparkShellTemplate = EngineConfig.getString("SPARK_SHELL");

                    StringBuffer sparkCoreConf = new StringBuffer();
                    StringBuffer sparkAppConf = new StringBuffer();
                    StringBuffer sparkSqlConf = new StringBuffer();

                    Map<String, String> sparkCoreConfMap = SparkConfig.getCoreConf();

                    for(String key: engineParams.keySet()){
                        if(sparkCoreConfMap.containsKey(key.toLowerCase())){
                            sparkCoreConf
                                    .append("--")
                                    .append(key.toLowerCase())
                                    .append(" ")
                                    .append(Optional.ofNullable(engineParams.get(key)).orElse(sparkCoreConfMap.get(key)))
                                    .append(" ");
                        }else if(key.toLowerCase().startsWith("spark.sql.")){
                            if(StringUtils.isNotEmpty(engineParams.get(key))){
                                sparkSqlConf
                                        .append("set")
                                        .append(" ")
                                        .append(key.toLowerCase())
                                        .append("=")
                                        .append(engineParams.get(key))
                                        .append(";")
                                        .append("\n");
                            }
                        }else if(key.toLowerCase().startsWith("spark.")){
                            if(StringUtils.isNotEmpty(engineParams.get(key))){
                                sparkAppConf
                                        .append("--conf ")
                                        .append(key.toLowerCase())
                                        .append("=")
                                        .append(engineParams.get(key))
                                        .append(" ");
                            }
                        }else {
                            if(StringUtils.isNotEmpty(engineParams.get(key))){
                                sparkSqlConf
                                        .append("set")
                                        .append(" ")
                                        .append(key.toLowerCase())
                                        .append("=")
                                        .append(engineParams.get(key))
                                        .append(";")
                                        .append("\n");
                            }
                        }
                    }

                    // 核心引擎参数补全
                    for (String key: sparkCoreConfMap.keySet()){
                        if(!engineParams.containsKey(key)){
                            sparkCoreConf
                                    .append("--")
                                    .append(key.toLowerCase())
                                    .append(" ")
                                    .append(sparkCoreConfMap.get(key))
                                    .append(" ");
                        }
                    }


                    if(environment == EnvironmentEnum.PRODUCTION) { // 测试脚本
                        Map<String,String> customParams = new HashMap<>();
                        for (String key : customParamValues.keySet()) {
                            customParams.put(key,taskInputParams.get(key));
                        }

                        Map<String, String> testScriptParams = new HashMap<>();
                        testScriptParams.put("coreConf", sparkCoreConf.toString());
                        testScriptParams.put("appConf", sparkAppConf.toString());
                        testScriptParams.put("sqlFile", testSqlFile);
                        testScriptParams.put("customParams",JSONUtils.getJSONObjectFromMap(customParams).toString());

                        String sparkShellScript = ShellTemplateProcessorUtils.processTemplate(sparkShellTemplate, testScriptParams);
                        sparkShellScript = CodeUtils.dosToUnix(sparkShellScript);
                        scriptInfo.setTestScript(sparkShellScript);

                        // 生成SQL脚本
                        FileUtils.writeToFile(testSqlFile, CodeUtils.dosToUnix(CodeUtils.addEngineParamsForSpark(code.replaceAll(dbName+"."+tableName,dbName+"_test."+tableName),sparkSqlConf.toString())));
                        FileDistributorUtils.distributeFile(testSqlFile,this.schedulerCluster,testSqlFile);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(testShellFile, sparkShellScript);
                        FileDistributorUtils.distributeFile(testShellFile,this.schedulerCluster,testShellFile);
                    } else if (environment == EnvironmentEnum.TEST) { // 生产流程

                        StringBuffer customParamsBuffer = new StringBuffer();
                        StringBuffer customArgsBuffer = new StringBuffer();
                        for (String key : customParamValues.keySet()) {
                            customParamsBuffer
                                    .append(key)
                                    .append("=")
                                    .append(customParamValues.get(key))
                                    .append("\n");

                            customArgsBuffer
                                    .append(key)
                                    .append("=")
                                    .append("${").append(key).append("}")
                                    .append(" ");
                        }

                        Map<String, String> prodScriptParams = new HashMap<>();
                        prodScriptParams.put("coreConf", sparkCoreConf.toString());
                        prodScriptParams.put("appConf", sparkAppConf.toString());
                        prodScriptParams.put("sqlFile", testSqlFile);
                        prodScriptParams.put("customParams", customParamsBuffer.toString());
                        prodScriptParams.put("customArgs",customArgsBuffer.deleteCharAt(customArgsBuffer.length() - 1).toString());

                        String sparkShellScript = ShellTemplateProcessorUtils.processTemplate(sparkShellTemplate, prodScriptParams);
                        sparkShellScript = CodeUtils.dosToUnix(sparkShellScript);
                        scriptInfo.setTestScript(sparkShellScript);

                        // 生成SQL脚本
                        FileUtils.writeToFile(prodSqlFile, CodeUtils.dosToUnix(CodeUtils.addEngineParamsForSpark(code,sparkSqlConf.toString())));
                        FileDistributorUtils.distributeFile(prodSqlFile,this.schedulerCluster,testSqlFile);

                        // 生成Shell脚本文件
                        FileUtils.writeToFile(prodShellFile, sparkShellScript);
                        FileDistributorUtils.distributeFile(prodShellFile,this.schedulerCluster,prodShellFile);
                    }
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
                                .append(String.valueOf(connectionConfig.get("port-test")));

                        testMysqlScriptParams.put("connection", testMysqlConnectionBuffer.toString());
                        testMysqlScriptParams.put("sqlFile", testSqlFile);

                        String testMysqlScript = ShellTemplateProcessorUtils.processTemplate(mysqlShellTemplate, testMysqlScriptParams);
                        testMysqlScript = CodeUtils.dosToUnix(testMysqlScript);
                        scriptInfo.setTestScript(testMysqlScript);

                        // 生成SQL脚本
                        FileUtils.writeToFile(testSqlFile, code.replaceAll(dbName+"."+tableName,dbName+"_test."+tableName));
                        FileDistributorUtils.distributeFile(testSqlFile,this.schedulerCluster,testSqlFile);

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
                                .append(String.valueOf(connectionConfig.get("port")));

                        prodMysqlScriptParams.put("connection", prodMysqlConnectionBuffer.toString());
                        prodMysqlScriptParams.put("sqlFile", prodSqlFile);

                        String prodMysqlScript = ShellTemplateProcessorUtils.processTemplate(mysqlShellTemplate, prodMysqlScriptParams);
                        prodMysqlScript = CodeUtils.dosToUnix(prodMysqlScript);
                        scriptInfo.setProdScript(prodMysqlScript);

                        // 生成SQL脚本
                        FileUtils.writeToFile(prodSqlFile, code.replaceAll(dbName+"."+tableName,dbName+"_test."+tableName));
                        FileDistributorUtils.distributeFile(prodSqlFile,this.schedulerCluster,prodSqlFile);

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
        logger.info("*******************************************************************************************");
        if(environment == EnvironmentEnum.TEST) {
            logger.info(String.format(
                    "\n本次任务 %s \n测试SQL脚本路径: %s \n测试SQL脚本: \n%s \n测试Shell脚本路径: %s\n测试Shell脚本如下: \n%s ",
                    dbName + "." + taskTag,
                    scriptInfo.getTestSqlFile(),
                    FileUtils.readFile(scriptInfo.getTestSqlFile()),
                    scriptInfo.getTestScriptFile(),
                    scriptInfo.getTestScript()
            ));
        }else if(environment == EnvironmentEnum.PRODUCTION) {
            logger.info(String.format(
                    "\n本次任务 %s \n生产SQL脚本路径: %s \n生产SQL脚本: \n%s \n生产Shell脚本路径: %s\n生产Shell脚本如下: \n%s ",
                    dbName + "." + taskTag,
                    scriptInfo.getProdSqlFile(),
                    FileUtils.readFile(scriptInfo.getProdSqlFile()),
                    scriptInfo.getProdScriptFile(),
                    scriptInfo.getProdScript()
            ));
        }
        logger.info("*******************************************************************************************");
        logger.info("");

        return scriptInfo;
    }

    @Override
    public ExecutionResult<ShellTask> execute(TaskInfo task) {

        ScriptInfo scriptInfo = buildCodeScript(task);

        System.out.println(scriptInfo.getTestScriptFile());
        System.out.println(scriptInfo.getTestSqlFile());
        System.out.println(scriptInfo.getProdScriptFile());
        System.out.println(scriptInfo.getProdSqlFile());

        return new ExecutionResult(true, "File written successfully.", scriptInfo);
    }

}
