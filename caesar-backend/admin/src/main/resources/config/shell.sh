#!/bin/bash
# DolphinScheduler任务中执行的Shell脚本模板

# 打印开始时间
echo "Task start at: $(date '+%Y-%m-%d %H:%M:%S')"

# 定义参数（如果有的话）
PARAM1=$1  # 任务传入的第一个参数
PARAM2=$2  # 任务传入的第二个参数

# 打印传入的参数
echo "Received parameters: PARAM1=$PARAM1, PARAM2=$PARAM2"

# 任务逻辑部分
# 执行你需要的命令或任务逻辑
echo "Executing task logic..."

# 示例：如果你需要调用某个系统命令，可以如下：
# ls -al /your/directory
# 或者调用其他服务，例如触发另一个任务，使用相应的命令工具

# 如果需要根据结果做判断
if [ "$PARAM1" == "success_condition" ]; then
    echo "Condition met, executing specific command..."
    # 执行对应的操作
else
    echo "Condition not met, exiting with failure"
    exit 1  # 退出状态非0表示任务失败
fi

# 打印结束时间
echo "Task end at: $(date '+%Y-%m-%d %H:%M:%S')"

# 正常结束
exit 0
