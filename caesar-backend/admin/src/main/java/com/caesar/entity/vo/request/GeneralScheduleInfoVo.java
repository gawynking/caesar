package com.caesar.entity.vo.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class GeneralScheduleInfoVo {

    String taskName;                // 调度对应任务名称
    Integer taskVersion;
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
    String period = "day";    // 调度周期
    String dateValue = "today"; // 依赖日期值

    List<Dependency> dependency;

    String ownerName; // 创建人标识
    String taskCode;
    Boolean isDelete = false;

    @Data
    public static class Dependency{
        String preScheduleCode;
        String preScheduleName;
        int joinType = 1;
        String ownerName;
    }

}
