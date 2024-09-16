package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarScheduleConfig extends BaseEntity{

    String taskName;                // 调度对应任务名称
    String taskVersion;             // 调度对应任务版本
    int scheduleCategory = 1;       // 调度类别: 1-DolphinScheduler 2-Hera
    int scheduleLevel = 1;          // 调度部署级别: 1-workflow 2-project
    String project;                 // 项目名称
    String scheduleCode;            // 调度唯一编码
    String scheduleName;            // 调度名称
    int releaseStatus = 2;          // 在线状态: 1-在线 2-离线
    int taskType = 1;               // 调度类型: 1-shell
    String scheduleParams;          // 任务参数
    int taskPriority = 2;           // 调度优先级
    Integer failRetryTimes = 5;     // 失败重试次数
    Integer failRetryInterval = 3;  // 失败重试间隔
    String beginTime = "00:15:00";  // 任务启动时间
    int ownerId;                    // 创建人
    int version;                    // 版本
    String period;                  // 调度周期
    String dateValue;               // 依赖日期值

}
