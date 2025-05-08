#!/bin/bash
source ~/.bash_profile
## Params: connection sqlFile

# 打印开始时间
echo "Task start at: $(date '+%Y-%m-%d %H:%M:%S')"

# 任务逻辑部分
mysql @{{ connection }} < @{{ sqlFile }}

# 获取 hive 的退出状态码
status=$?

# 根据状态码判断任务执行结果
if [ ${status} -eq 0 ]; then
    echo "MySQL job completed successfully."
else
    echo "MySQL job failed with exit code ${status}."
fi

# 打印结束时间
echo "Task end at: $(date '+%Y-%m-%d %H:%M:%S')"

# 退出
exit ${status}
