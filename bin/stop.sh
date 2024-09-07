#!/bin/bash
pid=`ps aux | grep java | grep caesar | awk '{print $2}'`

[ ! $pid ] && echo "找不到Caesar的进程,请确认Caesar已经启动" && exit 0

res=`kill -9 $pid`

echo 关闭caesar成功,pid:$pid
