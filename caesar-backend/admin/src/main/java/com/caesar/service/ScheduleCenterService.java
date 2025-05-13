package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarScheduleConfig;
import com.caesar.entity.bo.CaesarScheduleConfigInfoBo;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.entity.vo.response.ScheduleBaseInfoVo;
import com.caesar.entity.vo.response.ScheduleInfoVo;
import com.caesar.entity.vo.response.TaskDependency;
import com.caesar.exception.CaesarScheduleConfigSyncException;
import com.caesar.exception.SqlParseException;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;

import java.util.List;

public interface ScheduleCenterService extends IService<CaesarScheduleConfig> {

    List<ScheduleInfoVo> getTaskSchedules(String taskName);

    Boolean genTaskSchedule(GeneralScheduleInfoVo scheduleInfo);

    Boolean updateTaskScheduleDenpendency(String scheduleCode, GeneralScheduleInfoVo.Dependency dependency);

    Boolean updateTaskSchedule(GeneralScheduleInfoVo scheduleInfo);

    Boolean deleteTaskSchedule(String taskName);

    ScheduleBaseInfoVo getScheduleBaseInfo();

    ScheduleInfoVo getTaskSchedule(String scheduleName);

    List<TaskDependency> getTaskDependencies(String taskName, Integer taskVersion, String period) throws SqlParseException;

    List<CaesarScheduleConfigInfoBo> syncCaesarSchedulerConfig() throws CaesarScheduleConfigSyncException;
}