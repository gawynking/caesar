#!/bin/bash
work_dir=$(cd "$(dirname $0)";pwd)
if [[ ! -f "$work_dir/stop.sh" ]];then
	echo "找不到关闭Caesar的脚本stop.sh，请确保${work_dir}目录下有stop.sh脚本"
	exit 1
fi
if [[ ! -f "$work_dir/start.sh" ]];then
	echo "找不到启动Caesar的脚本start.sh，请确保${work_dir}目录下有start.sh脚本"
	exit 1
fi

sh $work_dir/stop.sh
sh $work_dir/start.sh

echo "---------重启Caesar成功----------"
