package com.caesar.model;

import com.caesar.enums.SchedulerEnum;
import com.caesar.enums.SchedulingPeriod;
import com.caesar.enums.TaskTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Data
@ToString
public class SchedulerModel {

    // 调度系统
    String system; // 例如: dolphinscheduler
    SchedulerEnum schedulerEnum; // 例如: DOLPHINSCHEDULER

    // 调度项目
    String project; // 例如: caesar___caesar

    // 节点名称,对应调度任务名称
    String taskNodeName; // 例如: dwd.dwd_ord_order_di.day

    // 任务类型
    TaskTypeEnum taskType = TaskTypeEnum.SHELL;

    // 优先级
    String priority = "MEDIUM";

    // 重试次数
    int retryTimes = 5;

    // 重试间隔
    int retryInterval = 1;

    // 开始时间
    String beginTime = "00:15:00";

    // 依赖任务
    List<DependencyModel> dependency = new ArrayList<>(); // 例如: ["ods.ods__order__di","ods.ods__order_detail__di"]

    // 参数,废弃参数
    String globalParams = "[]";

    // 超时时间
    int timeout = 0;

    // 描述
    String description = "";

    // 执行类型
    String executionType = "PARALLEL";

    // 执行脚本
    String execTaskScript; // 例如: sh hive.sh

    // 任务上下线 1-上线 0-下线
    int ReleaseState = 0;

    // 是否删除任务, true从调度系统删除任务
    Boolean isDelete = false;

    // 调度周期 hour day week month
    String schedulingPeriod = "day";

    // 调度周期间隔值,间隔数,整数值
    int scheduleInterval = 0; // 默认说明: 小时调度 1-表示从每个小时01分钟开始执行调度; 天调度 15-从每天零点15开始执行; 周调度 6-标识每周六执行; 月调度 1-标识每月1号执行

}
