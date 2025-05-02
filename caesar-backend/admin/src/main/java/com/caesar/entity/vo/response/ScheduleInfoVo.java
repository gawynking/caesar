package com.caesar.entity.vo.response;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleInfoVo {

    int scheduleCategory = 1;
    String project;
    String scheduleCode;
    String scheduleName;
    int releaseStatus = 2; // 1-在线 2-下线
    int taskType = 1;
    String scheduleParams;
    int taskPriority = 2;
    Integer failRetryTimes = 5;
    Integer failRetryInterval = 3;
    String beginTime = "00:15:00";
    int version;
    String period;                  // 调度周期
    String dateValue;               // 依赖日期值

    String ownerName; // 创建人标识

    List<Dependency> dependency;

    @Data
    public static class Dependency{
        String preScheduleCode;
        String preScheduleName;
        int joinType = 1;
        int ownerId;
        String ownerName;
    }
}
