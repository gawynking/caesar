package com.caesar;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public abstract class AbstractSqlExecutor<T> {

    private static final Logger logger = Logger.getLogger(AbstractSqlExecutor.class.getName());


    protected T t;


    public final void execute(
            String[] args,
            HandlerCallback<T> handler
    ) {

        // 1 连接数据库
        t = openConnect();

        // 2 解析参数
        Map<String, String> params = parseParams(args);

        // 3 获取SQL列表
        List<String> sqls = readSqlFromSqlFile(params.get("sql-file"));

        // 3 替换参数
        for(String sql:sqls) {
            String finalSql = replaceParams(sql, params);

            // 4 执行SQL
            executeSQL(t,finalSql,handler);
        }

        // 5 关闭连接
        closeConnect(t);
    }

    // 1 解析外部参数
    private static Map<String, String> parseParams(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            String[] keyValue = args[i].split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
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
                sqlBuilder.append(line);
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    sqls.add(sql);
                    sqlBuilder.setLength(0); // 清空 StringBuilder
                }
            }
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
    protected abstract T openConnect();

    // 5 执行 SQL
    protected abstract void executeSQL(T t, String sql,HandlerCallback<T> handler);

    // 6 关闭连接
    protected abstract void closeConnect(T t);


    // 7 定义结果处理接口
    public static interface HandlerCallback<T> {
        void process(T t,String sql) throws Exception;
    }


}
