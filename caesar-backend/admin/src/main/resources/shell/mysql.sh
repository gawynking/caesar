#!/bin/bash
source ~/.bash_profile

# 打印开始时间
echo "Task start at: $(date '+%Y-%m-%d %H:%M:%S')"

# 任务逻辑部分
mysql @{{ connection }} < @{{ sqlFile }}

# 打印结束时间
echo "Task end at: $(date '+%Y-%m-%d %H:%M:%S')"

# 获取 hive 的退出状态码
status=$?

# 根据状态码判断任务执行结果
if [ ${status} -eq 0 ]; then
    echo "Spark job completed successfully."
    # 可以在这里添加成功时需要执行的操作
else
    echo "Spark job failed with exit code ${status}."
    # 可以在这里添加失败时需要执行的操作
fi

# 退出
exit ${status}
