package com.caesar.spark;


import com.caesar.AbstractSqlExecutor;
import org.apache.spark.sql.SparkSession;

import java.util.logging.Logger;


/**
 * Spark SQL提交入口
 */
public class SparkSQLExecutor extends AbstractSqlExecutor<SparkSession> {

    private static final Logger logger = Logger.getLogger(SparkSQLExecutor.class.getName());


    public static void main(String[] args) {
        new SparkSQLExecutor().execute(args, new MetaCallback<SparkSession>() {
            @Override
            public void process(SparkSession sparkSession, String sql) throws Exception {
                sparkSession.sql(sql).printSchema();
            }
        });
    }


    @Override
    protected SparkSession openConnect() {
        return SparkSession.builder()
                .appName("Spark SQL Executor Java")
                .enableHiveSupport()  // 如果需要 Hive 支持
                .getOrCreate();
    }

    @Override
    protected void executeSQL(SparkSession sparkSession, String sql) {
        try {
            sparkSession.sql(sql);
        } catch (Exception e) {
            logger.warning("Error executing SQL: " + sql + ". Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    protected void closeConnect(SparkSession sparkSession) {
        try {
            sparkSession.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
