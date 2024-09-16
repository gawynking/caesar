package com.caesar.util;

import com.alibaba.fastjson.JSONArray;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.enums.PriorityEnum;
import com.caesar.enums.SchedulerEnum;
import com.caesar.enums.SchedulingPeriod;
import com.caesar.enums.TaskTypeEnum;
import com.caesar.model.DependencyModel;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerFacade;

import java.util.ArrayList;
import java.util.List;

public class CaesarScheduleUtils {


    public static List<String> getDependencyList(String jsonArrayString){
        JSONArray dependency = JSONUtils.getJSONArrayfromString(jsonArrayString);
        List<String> list = dependency.toJavaList(String.class);
        return list;
    }

    public static SchedulerModel getDeleteScheduleModel(String scheduleName){
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setTaskNodeName(scheduleName);
        schedulerModel.setIsDelete(true);
        return schedulerModel;
    }


    public static SchedulerModel convertScheduleConfig(GeneralScheduleInfoVo scheduleInfo){
        SchedulerModel schedulerModel = new SchedulerModel();

        schedulerModel.setSystem(scheduleInfo.getScheduleCategory()==1?"DolphinScheduler":"Hera");
        schedulerModel.setSchedulerEnum(scheduleInfo.getScheduleCategory()==1? SchedulerEnum.DOLPHINSCHEDULER:SchedulerEnum.HERA);
        schedulerModel.setProject(scheduleInfo.getProject());
        schedulerModel.setTaskNodeName(scheduleInfo.getScheduleName());
        schedulerModel.setTaskType(TaskTypeEnum.SHELL);
        schedulerModel.setPriority(PriorityEnum.fromCode(scheduleInfo.getTaskPriority()).toString().toUpperCase());
        schedulerModel.setRetryTimes(scheduleInfo.getFailRetryTimes());
        schedulerModel.setRetryInterval(scheduleInfo.getFailRetryInterval());
        schedulerModel.setBeginTime(scheduleInfo.getBeginTime());
        schedulerModel.setGlobalParams(scheduleInfo.getScheduleParams()); // 格式
        schedulerModel.setReleaseState(1);
        schedulerModel.setIsDelete(scheduleInfo.getIsDelete());

        List<DependencyModel> dependencyModels = new ArrayList<>();
        for(GeneralScheduleInfoVo.Dependency dependency:scheduleInfo.getDependency()){
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

    public static SchedulerFacade getSchedulerClient(){
        SchedulerFacade schedulerFacade = SchedulerUtils.getScheduler();
        return schedulerFacade;
    }

}
