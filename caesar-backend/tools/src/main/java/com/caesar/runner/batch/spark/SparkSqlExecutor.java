package com.caesar.runner.batch.spark;



import com.caesar.runner.AbstractSqlExecutor;
import com.caesar.runner.extend.SqlExtendCreateTableFromFile;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

import java.util.Map;
import java.util.logging.Logger;


/**
 * Spark SQL提交入口
 */
public class SparkSqlExecutor extends AbstractSqlExecutor<SparkSession> {

    private static final Logger logger = Logger.getLogger(SparkSqlExecutor.class.getName());


    public static void main(String[] args) {
        new SparkSqlExecutor().execute(args, new HandlerCallback<SparkSession>() {
            @Override
            public void process(SparkSession sparkSession, String sql) throws Exception {
                SqlExtendCreateTableFromFile sqlExtendCreateTableFromFile = new SqlExtendCreateTableFromFile();
                if(sqlExtendCreateTableFromFile.validateSqlSyntax(sql)){
                    SqlExtendCreateTableFromFile.ExtendSqlModel extendSql = sqlExtendCreateTableFromFile.parseMeta(sql);
                    switch (extendSql.getFileType()){
                        case JSON:
                            sparkSession.read().json(extendSql.getFilePath()).createOrReplaceTempView(extendSql.getTableName());
                            break;
                        case CSV:
                            sparkSession.read().csv(extendSql.getFilePath()).createOrReplaceTempView(extendSql.getTableName());
                            break;
                        case TXT:
                            sparkSession.read().text(extendSql.getFilePath()).createOrReplaceTempView(extendSql.getTableName());
                            break;
                    }
                }else {
                    sparkSession.sql(sql).show();
                }
            }
        });
    }


    @Override
    protected SparkSession openConnect(String taskName) {
        if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")) {
            return SparkSession.builder()
                    .appName(taskName)
                    .master("local[*]")
                    .getOrCreate();
        }else {
            SparkConf conf = new SparkConf()
                    .set("spark.sql.shuffle.partitions", "512")
                    .set("hive.exec.dynamic.partition", "true")
                    .set("hive.exec.dynamic.partition.mode", "nonstrict")
                    .setAppName(taskName);

            return SparkSession.builder()
                    .enableHiveSupport()
                    .config(conf)
                    .getOrCreate();
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

    @Override
    protected void alert(String clusterTag, String taskName, Integer counter, String sql) {

    }

    @Override
    protected Boolean enableLogging() {
        return true;
    }

    @Override
    protected void persistExecutionLog(Map<String, Object> logMap) {
        logger.info(logMap.toString());
    }

}
