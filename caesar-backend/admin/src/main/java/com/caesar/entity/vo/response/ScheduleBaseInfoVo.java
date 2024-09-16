package com.caesar.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleBaseInfoVo {

    int scheduleCategory = 1;       // 调度类别: 1-DolphinScheduler 2-Hera
    int scheduleLevel = 1;          // 调度部署级别: 1-workflow 2-project
    String project;                 // 项目名称

}
