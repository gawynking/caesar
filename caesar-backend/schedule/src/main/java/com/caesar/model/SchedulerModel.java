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

    // 节点名称
    String taskNodeName;

    // 任务类型
    TaskTypeEnum taskType = TaskTypeEnum.SHELL;

    // 优先级
    String priority;

    // 重试次数
    int retryTimes = 5;

    // 重试间隔
    int retryInterval = 1;

    // 开始时间
    String beginTime = "00:15:00";

    // 依赖任务
    List<DependencyModel> dependency;

    // 参数
    String globalParams = "[]";

    // 超时时间
    int timeout = 0;

    // 描述
    String description = "";

    // 执行类型
    String executionType = "PARALLEL";

    // 脚本
    String taskScript;

    // 任务上下线
    int ReleaseState = 0;

    boolean isDelete = false;

}
