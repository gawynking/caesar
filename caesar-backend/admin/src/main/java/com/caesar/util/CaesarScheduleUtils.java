package com.caesar.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caesar.config.SchedulerConfig;
import com.caesar.config.SchedulerConstant;
import com.caesar.entity.CaesarScheduleDependency;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.bo.CaesarScheduleConfigInfoBo;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.enums.PriorityEnum;
import com.caesar.enums.SchedulerEnum;
import com.caesar.enums.SchedulingPeriod;
import com.caesar.enums.TaskTypeEnum;
import com.caesar.model.DependencyModel;
import com.caesar.model.SchedulerModel;
import com.caesar.model.code.TemplateUtils;
import com.caesar.model.code.enums.DatePeriod;
import com.caesar.runner.params.TaskInfo;
import com.caesar.scheduler.SchedulerFacade;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CaesarScheduleUtils {


    public static List<String> getDependencyList(String jsonArrayString) {
        JSONArray dependency = JSONUtils.getJSONArrayfromString(jsonArrayString);
        List<String> list = dependency.toJavaList(String.class);
        return list;
    }

    public static SchedulerModel getDeleteScheduleModel(String scheduleName) {
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setTaskNodeName(scheduleName);
        schedulerModel.setIsDelete(true);
        return schedulerModel;
    }


    public static SchedulerModel convertScheduleConfigFromScheduleInfoVo(GeneralScheduleInfoVo scheduleInfo) {
        SchedulerModel schedulerModel = new SchedulerModel();

        schedulerModel.setSystem(scheduleInfo.getScheduleCategory() == 1 ? "DolphinScheduler" : "Hera");
        schedulerModel.setSchedulerEnum(scheduleInfo.getScheduleCategory() == 1 ? SchedulerEnum.DOLPHINSCHEDULER : SchedulerEnum.HERA);
        schedulerModel.setProject(scheduleInfo.getProject());
        schedulerModel.setTaskNodeName(scheduleInfo.getScheduleName());
        schedulerModel.setTaskType(TaskTypeEnum.SHELL);
        schedulerModel.setPriority(PriorityEnum.fromCode(scheduleInfo.getTaskPriority()).toString().toUpperCase());
        schedulerModel.setRetryTimes(scheduleInfo.getFailRetryTimes());
        schedulerModel.setRetryInterval(scheduleInfo.getFailRetryInterval());
        schedulerModel.setBeginTime(scheduleInfo.getBeginTime());
        schedulerModel.setGlobalParams(scheduleInfo.getScheduleParams()); // 格式
        schedulerModel.setReleaseState(scheduleInfo.getReleaseStatus()==1?1:0);
        schedulerModel.setIsDelete(scheduleInfo.getIsDelete());

        List<DependencyModel> dependencyModels = new ArrayList<>();
        for (GeneralScheduleInfoVo.Dependency dependency : Optional.ofNullable(scheduleInfo.getDependency()).orElse(new ArrayList<>())) {
            DependencyModel dependencyModel = new DependencyModel();
            dependencyModel.setDependency(dependency.getPreScheduleName());
            dependencyModel.setPeriod(SchedulingPeriod.fromString(scheduleInfo.getPeriod()));
            dependencyModel.setDateValue(scheduleInfo.getDateValue());
            dependencyModels.add(dependencyModel);
        }
        schedulerModel.setDependency(dependencyModels);

        schedulerModel.setExecTaskScript(scheduleInfo.getTaskCode());

        return schedulerModel;
    }


    public static SchedulerModel convertScheduleConfigFromScheduleInfoBo(
            CaesarScheduleConfigInfoBo scheduleConfigInfoBo,
            List<CaesarScheduleConfigInfoBo> scheduleConfigInfoBos,
            CaesarTask taskInfo
    ) throws Exception {
        SchedulerModel schedulerModel = new SchedulerModel();

        schedulerModel.setSystem(scheduleConfigInfoBo.getScheduleCategory() == 1 ? "DolphinScheduler" : "Hera");
        schedulerModel.setSchedulerEnum(scheduleConfigInfoBo.getScheduleCategory() == 1 ? SchedulerEnum.DOLPHINSCHEDULER : SchedulerEnum.HERA);
        schedulerModel.setProject(scheduleConfigInfoBo.getProject());
        schedulerModel.setTaskNodeName(scheduleConfigInfoBo.getScheduleName());
        schedulerModel.setTaskType(TaskTypeEnum.SHELL);
        schedulerModel.setPriority(PriorityEnum.fromCode(scheduleConfigInfoBo.getTaskPriority()).toString().toUpperCase());
        schedulerModel.setRetryTimes(scheduleConfigInfoBo.getFailRetryTimes());
        schedulerModel.setRetryInterval(scheduleConfigInfoBo.getFailRetryInterval());
        schedulerModel.setBeginTime(scheduleConfigInfoBo.getBeginTime());
        schedulerModel.setGlobalParams(scheduleConfigInfoBo.getScheduleParams()); // 格式
        schedulerModel.setReleaseState(scheduleConfigInfoBo.getReleaseStatus()==1?1:0);
        schedulerModel.setIsDelete(false);
        schedulerModel.setSchedulingPeriod(scheduleConfigInfoBo.getPeriod());
        if (scheduleConfigInfoBo.getPeriod().equals("hour")) {
            schedulerModel.setScheduleInterval(1);
        } else if (scheduleConfigInfoBo.getPeriod().equals("day")) {
            schedulerModel.setScheduleInterval(15);
        } else if (scheduleConfigInfoBo.getPeriod().equals("week")) {
            schedulerModel.setScheduleInterval(6);
        } else if (scheduleConfigInfoBo.getPeriod().equals("month")) {
            schedulerModel.setScheduleInterval(1);
        } else {
            throw new RuntimeException();
        }

        List<DependencyModel> dependencyModels = new ArrayList<>();
        for (CaesarScheduleDependency dependency : Optional.ofNullable(scheduleConfigInfoBo.getDependencies()).orElse(new ArrayList<>())) {
            DependencyModel dependencyModel = new DependencyModel();
            String preScheduleName = null;
            for (CaesarScheduleConfigInfoBo tmpScheduleConfigInfoBo : scheduleConfigInfoBos) {
                if (dependency.getPreScheduleCode().equals(tmpScheduleConfigInfoBo.getScheduleCode())) {
                    preScheduleName = tmpScheduleConfigInfoBo.getScheduleName();
                }
            }
            dependencyModel.setDependency(preScheduleName); // 名称
            dependencyModel.setPeriod(SchedulingPeriod.fromString(scheduleConfigInfoBo.getPeriod())); // workflow无效
            dependencyModel.setDateValue(scheduleConfigInfoBo.getDateValue()); // workflow无效
            dependencyModels.add(dependencyModel);
        }
        schedulerModel.setDependency(dependencyModels);

        // 获取调度执行脚本
        String executeScript = getTaskProductionExecuteShellScript(scheduleConfigInfoBo.getPeriod(), taskInfo);
        schedulerModel.setExecTaskScript(executeScript);

        return schedulerModel;
    }


    public static String getTaskProductionExecuteShellScript(String period, CaesarTask taskInfo) {
        CaesarTaskExecuteRecordDto taskExecuteRecordDto = new CaesarTaskExecuteRecordDto();
        JSONObject parameter = JSONUtils.getJSONObjectFromMap(TemplateUtils.generalRefreshParameter(DatePeriod.fromKey(period), new Date()));
        taskExecuteRecordDto.setParameter(parameter.toJSONString());
        taskExecuteRecordDto.setEnvironment("production");
        taskExecuteRecordDto.setPeriod(period);
        TaskInfo task = TaskUtils.generalTaskInfo(taskExecuteRecordDto, taskInfo);
        String executeScript = EngineUtils.getTaskShellExecuteScript(task);
        return executeScript;
    }


    public static SchedulerFacade getSchedulerClient() {
        SchedulerFacade schedulerFacade = SchedulerUtils.getScheduler();
        return schedulerFacade;
    }


    /**
     * [
     * {
     * "id": 1,
     * "name": "",
     * "processDefinitionVersion": 1,
     * "projectCode": 17606131924800,
     * "processDefinitionCode": 17606141664448,
     * "preTaskCode": 0,
     * "preTaskVersion": 0,
     * "postTaskCode": 17606133204800,
     * "postTaskVersion": 1,
     * "conditionType": "NONE",
     * "conditionParams": {},
     * "createTime": "2025-05-11 23:46:21",
     * "updateTime": "2025-05-11 23:46:21",
     * "operator": 1,
     * "operateTime": "2025-05-11 23:46:21"
     * }
     * ]
     * <p>
     * [
     * {
     * "id": 1,
     * "code": 17606133204800,
     * "name": "START_NODE",
     * "version": 1,
     * "description": "",
     * "projectCode": 17606131924800,
     * "userId": 1,
     * "taskType": "SHELL",
     * "taskParams": {
     * "localParams": [],
     * "rawScript": "echo \"START NODE Execute.\"",
     * "resourceList": []
     * },
     * "taskParamList": [],
     * "taskParamMap": null,
     * "flag": "YES",
     * "isCache": "NO",
     * "taskPriority": "MEDIUM",
     * "userName": null,
     * "projectName": null,
     * "workerGroup": "default",
     * "environmentCode": -1,
     * "failRetryTimes": 3,
     * "failRetryInterval": 1,
     * "timeoutFlag": "CLOSE",
     * "timeoutNotifyStrategy": null,
     * "timeout": 0,
     * "delayTime": 0,
     * "resourceIds": null,
     * "createTime": "2025-05-11 23:46:21",
     * "updateTime": "2025-05-11 23:46:21",
     * "modifyBy": null,
     * "taskGroupId": 0,
     * "taskGroupPriority": 0,
     * "cpuQuota": -1,
     * "memoryMax": -1,
     * "taskExecuteType": "BATCH",
     * "operator": 1,
     * "operateTime": "2025-05-11 23:46:21",
     * "dependence": ""
     * }
     * ]
     *
     * @param processTaskDefine
     * @return
     */
    public static List<CaesarScheduleConfigInfoBo> parseDolphinSchedulerSchedulers(JSONObject processTaskDefine) {

        List<CaesarScheduleConfigInfoBo> scheduleSystemSchedulerConfigs = new ArrayList<>();

        JSONArray taskDefinitionList = Optional.ofNullable(processTaskDefine.getJSONObject("data").getJSONArray("taskDefinitionList")).orElse(new JSONArray());
        JSONArray processTaskRelationList = Optional.ofNullable(processTaskDefine.getJSONObject("data").getJSONArray("processTaskRelationList")).orElse(new JSONArray());

        for (int i = 0; i < taskDefinitionList.size(); i++) {

            JSONObject schedulerConfig = taskDefinitionList.getJSONObject(i);


            CaesarScheduleConfigInfoBo caesarScheduleConfigInfoBo = new CaesarScheduleConfigInfoBo();

//            caesarScheduleConfigInfoBo.setId(); // 通过 code 关联获取
//            caesarScheduleConfigInfoBo.setTaskName(); // 通过 code 关联获取
//            caesarScheduleConfigInfoBo.setTaskVersion(); // 通过 code 关联获取
            if (SchedulerConfig.getString(SchedulerConstant.SCHEDULER_TYPE).toLowerCase().equals("dolphin")) {
                caesarScheduleConfigInfoBo.setScheduleCategory(1);
            } else {
                continue;
            }
            if (SchedulerConfig.getString(SchedulerConstant.SCHEDULER_LEVEL).toLowerCase().equals("workflow")) {
                caesarScheduleConfigInfoBo.setScheduleLevel(1);
            } else {
                continue;
            }
//            caesarScheduleConfigInfoBo.setProject(); // 外部获取
//            caesarScheduleConfigInfoBo.setScheduleCode(schedulerConfig.getString("code")); // 两边scheduleCode代表并不相等
            caesarScheduleConfigInfoBo.setScheduleName(schedulerConfig.getString("name"));
            if (schedulerConfig.getString("flag").equals("YES")) {
                caesarScheduleConfigInfoBo.setReleaseStatus(1);
            } else {
                caesarScheduleConfigInfoBo.setReleaseStatus(2);
            }
            if (schedulerConfig.getString("taskType").equals("SHELL")) {
                caesarScheduleConfigInfoBo.setTaskType(1);
            } else {
                continue;
            }

//            caesarScheduleConfigInfoBo.setScheduleParams(schedulerConfig.getString("taskParams")); // 这俩参数并不相等
            if (schedulerConfig.getString("taskPriority").equals("HIGHEST") || schedulerConfig.getString("taskPriority").equals("HIGH")) {
                caesarScheduleConfigInfoBo.setTaskPriority(3);
            } else if (schedulerConfig.getString("taskPriority").equals("MEDIUM")) {
                caesarScheduleConfigInfoBo.setTaskPriority(2);
            } else {
                caesarScheduleConfigInfoBo.setTaskPriority(1);
            }
            caesarScheduleConfigInfoBo.setFailRetryTimes(schedulerConfig.getInteger("failRetryTimes"));
            caesarScheduleConfigInfoBo.setFailRetryInterval(schedulerConfig.getInteger("failRetryInterval"));
            caesarScheduleConfigInfoBo.setBeginTime("00:15:00");
            caesarScheduleConfigInfoBo.setOwnerId(-1); // 小于0 表示 系统创建
//            caesarScheduleConfigInfoBo.setVersion(); // 通过 code 关联获取
//            caesarScheduleConfigInfoBo.setPeriod(); // project级别参数，workflow 模式无效
//            caesarScheduleConfigInfoBo.setDateValue(); // project级别参数，workflow 模式无效
            caesarScheduleConfigInfoBo.setGenType(2); // 默认

            try {
                caesarScheduleConfigInfoBo.setCreateTime(new Timestamp(DateUtils.dateParse(schedulerConfig.getString("createTime"), "yyyy-MM-dd HH:mm:ss").getTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


            // 处理依赖信息
            List<CaesarScheduleDependency> dependencies = new ArrayList<>();
            for (int j = 0; j < processTaskRelationList.size(); j++) {
                JSONObject tmpDependency = processTaskRelationList.getJSONObject(j);
                if (schedulerConfig.getString("code").equals(tmpDependency.getString("postTaskCode")) && tmpDependency.getLong("preTaskCode") > 0L) {
                    CaesarScheduleDependency dependency = new CaesarScheduleDependency();
//                    dependency.setId();
                    dependency.setScheduleCode(schedulerConfig.getString("code"));
                    dependency.setPreScheduleCode(tmpDependency.getString("preTaskCode"));
                    dependency.setJoinType(1); // 加入方式: 1-自动识别 2-人工加入
                    dependency.setOwnerId(-1); // 小于0 表示 系统创建
                    try {
                        dependency.setCreateTime(new Timestamp(DateUtils.dateParse(tmpDependency.getString("createTime"), "yyyy-MM-dd HH:mm:ss").getTime()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    dependencies.add(dependency);
                }
            }
            caesarScheduleConfigInfoBo.setDependencies(dependencies);

            scheduleSystemSchedulerConfigs.add(caesarScheduleConfigInfoBo);
        }


        return scheduleSystemSchedulerConfigs;
    }


}
