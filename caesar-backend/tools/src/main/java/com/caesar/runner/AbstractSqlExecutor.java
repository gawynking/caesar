package com.caesar.runner;


import com.caesar.util.DateUtils;
import com.caesar.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.caesar.config.Constants;



public abstract class AbstractSqlExecutor<T> implements SqlExecutor<T> {

    private static final Logger logger = Logger.getLogger(AbstractSqlExecutor.class.getName());

    // 集群标识，定义任务执行在哪里
    protected String clusterTag;

    private String separator = File.separator;
    protected T t;
    private Integer counter = 0;

    @Override
    public final void execute(String[] args, HandlerCallback<T> handler) {

        Map<String, Object> outerMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();

        // 1 解析参数
        Map<String, String> params = parseParams(args);

        clusterTag = params.get(Constants.CLUSTER_TAG);
        if(null == clusterTag) clusterTag = "unknown";
        String sqlFile = params.get(Constants.SUBMIT_SQL_FILE);
        if(StringUtils.isEmpty(sqlFile)){
            throw new RuntimeException("任务没有正确指定SQL文件目录地址: " + Constants.SUBMIT_SQL_FILE + " = []");
        }
        String[] split = null;
        if (System.getProperty("os.name").contains("Windows")) {
            split = sqlFile.split("\\\\");
        }else{
            split = sqlFile.split(separator);
        }
        String taskName = split[split.length-1].replace(".sql","");
        // 2 连接数据库
        t = openConnect(taskName);

        // 3 获取SQL列表
        List<String> sqls = readSqlFromSqlFile(sqlFile);

        long startTime = System.currentTimeMillis();
        // 3 替换参数
        for(String sql:sqls) {
            String finalSql = replaceParams(sql, params);

            long time1 = System.currentTimeMillis();
            // 4 执行SQL
            this.addCounter();
            try {
                executeSQL(t,finalSql,handler);
            } catch (Exception e) {
                if(enableAlert()){
                    alert(clusterTag,taskName,getCounter(),sql);
                }
                logger.warning("Error executing SQL: " + sql + ". Error: " + e.getMessage());
                e.printStackTrace();
            }
            long time2 = System.currentTimeMillis();

            if(enableLogging()) {
                innerMap.put("sequence",getCounter().toString());
                innerMap.put("sql", finalSql);
                innerMap.put("sql_duration", String.valueOf(time2 - time1));
            }
        }
        long endTime = System.currentTimeMillis();
        if(enableLogging()) {
            outerMap.put("task_name",taskName);
            outerMap.put("total",getCounter());
            outerMap.put("details",innerMap);
            outerMap.put("start_time", DateUtils.fromUnixtime(startTime,"yyyy-MM-dd HH:mm:ss"));
            outerMap.put("end_time", DateUtils.fromUnixtime(endTime,"yyyy-MM-dd HH:mm:ss"));
            outerMap.put("task_duration", String.valueOf(endTime - startTime));

            persistExecutionLog(outerMap);
        }

        // 5 关闭连接
        closeConnect(t);
    }


    // 1 解析外部参数,参数格式: key=value
    private static Map<String, String> parseParams(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String[] keyValue = args[i].split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return params;
    }


    // 2 从SQL文件中读取SQL
    private List<String> readSqlFromSqlFile(String sqlFile){
        List<String> sqls = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过空行和注释
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }

                // 累积 SQL 语句，遇到分号时执行
                sqlBuilder.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().replace(";","").trim();
                    if(StringUtils.isNotEmpty(sql)) sqls.add(sql);
                    sqlBuilder.setLength(0); // 清空 StringBuilder
                }
            }
            String sql = sqlBuilder.toString().replace(";","");
            if(StringUtils.isNotEmpty(sql)) sqls.add(sql);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqls;
    }


    // 3 替换 SQL 模板中的参数
    private static String replaceParams(String sql, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sql = sql.replace("${" + key + "}", value);
        }
        return sql;
    }


    // 4 获取连接对象
    protected abstract T openConnect(String taskName);

    // 5 执行 SQL
    protected void executeSQL(T t, String sql, HandlerCallback<T> handler) throws Exception {
        handler.process(t,sql);
    }

    // 6 关闭连接
    protected abstract void closeConnect(T t);

    protected Boolean enableAlert(){
        return false;
    }

    protected abstract void alert(String clusterTag ,String taskName, Integer counter, String sql);

    protected Boolean enableLogging(){
         return false;
    }

    protected abstract void persistExecutionLog(Map<String,Object> logMap);

    public void addCounter(){
        this.counter++;
    }

    public Integer getCounter(){
        return counter;
    }

}
