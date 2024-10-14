#!/bin/bash

# 打印开始时间
echo "Task start at: $(date '+%Y-%m-%d %H:%M:%S')"

# 自定义参数
#start_date=$1  # 任务传入的第一个参数
#end_date=$2  # 任务传入的第二个参数
{{ customParamsDefine }}

# 打印传入的参数
echo "Received parameters: start_date=${start_date}, end_date=${end_date}"

# 任务逻辑部分
spark-submit --master yarn --deploy-mode cluster {{ engineParamsSetter }} {{ customParamsSetter }} --class com.caesar.spark.sql.SparkSqlSubmit /opt/caesar/lib/spark-plugin.jar {{ sqlFile }}


# 获取 spark-submit 的退出状态码
status=$?

# 根据状态码判断任务执行结果
if [ ${status} -eq 0 ]; then
    echo "Spark job completed successfully."
    # 可以在这里添加成功时需要执行的操作
else
    echo "Spark job failed with exit code ${status}."
    # 可以在这里添加失败时需要执行的操作
fi

# 打印结束时间
echo "Task end at: $(date '+%Y-%m-%d %H:%M:%S')"

# 正常结束
exit 0
