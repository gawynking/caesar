package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarScheduleConfig;
import com.caesar.entity.bo.CaesarScheduleConfigInfoBo;
import com.caesar.entity.dto.CaesarScheduleConfigDto;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.entity.vo.response.ScheduleBaseInfoVo;
import com.caesar.entity.vo.response.ScheduleInfoVo;
import com.caesar.entity.vo.response.TaskDependency;
import com.caesar.exception.CaesarScheduleConfigSyncException;
import com.caesar.exception.SqlParseException;

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

    CaesarScheduleConfigDto findScheduleConfigFromScheduleName(String scheduleName);

    Boolean validateTaskDeploySchedule(String taskName);

    void releaseTask(int taskId);

    Boolean releaseScheduleByWorkflow(String scheduleName, Integer releaseState);

    Boolean onlineScheduleByWorkflow(String scheduleName);

    Boolean offlineScheduleByWorkflow(String scheduleName);
}