#!/bin/bash
source ~/.bash_profile

# 打印开始时间
echo "Task start at: $(date '+%Y-%m-%d %H:%M:%S')"

# 自定义参数
spark_jar=${CAESAR_HOME}/tool/tools-0.1.0.jar
@{{customParams}}

# 任务逻辑部分
#spark-submit \
#  --master yarn \
#  --deploy-mode cluster \
#  --name XXX \
#  --driver-memory 4g \
#  --driver-cores 1 \
#  --executor-memory 4g \
#  --executor-cores 2 \
#  --num-executors 4 \
#  --conf spark.yarn.executor.memoryOverhead=4g \
#  --conf spark.dynamicAllocation.enabled=true \
#  --conf spark.network.timeout=600s \
#  --class XXX \
#  /opt/XXX.jar

spark-submit --name @{{ appName }} --master yarn --deploy-mode cluster --class com.caesar.runner.batch.spark.SparkSqlExecutor  @{{ coreConf }} @{{ appConf }} ${spark_jar} sql.file.path=@{{ sqlFile }} @{{ customArgs }}

# 获取 hive 的退出状态码
status=$?

# 根据状态码判断任务执行结果
if [ ${status} -eq 0 ]; then
    echo "Spark job completed successfully."
else
    echo "Spark job failed with exit code ${status}."
fi

# 打印结束时间
echo "Task end at: $(date '+%Y-%m-%d %H:%M:%S')"

# 退出
exit ${status}