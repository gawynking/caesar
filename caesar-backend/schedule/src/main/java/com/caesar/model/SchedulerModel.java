package com.caesar.model;

import com.caesar.enums.SchedulerEnum;
import com.caesar.enums.TaskTypeEnum;
import lombok.Data;

import java.util.List;


@Data
public class SchedulerModel {

    // 调度系统
    String system;
    SchedulerEnum schedulerEnum;

    // 调度项目
    String project;

    // 工作流名称
    String workFlowName;

    // 节点名称
    String taskNodeName;

    // 任务类型
    TaskTypeEnum taskType;

    // 优先级
    String priority;

    // 重试次数
    int retryTimes;

    // 重试间隔
    int retryInterval;

    // 开始时间
    String beginTime;

    // 依赖任务
    List<String> dependency;

    // 参数
    String params;

}
