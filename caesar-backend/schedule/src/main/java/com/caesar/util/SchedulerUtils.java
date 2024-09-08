package com.caesar.util;

import com.caesar.config.SchedulerConfig;
import com.caesar.config.SchedulerConstant;
import com.caesar.enums.SchedulerEnum;
import com.caesar.scheduler.SchedulerFacade;


public class SchedulerUtils {


    public static SchedulerFacade getScheduler(){
        SchedulerFacade schedulerFacade = new SchedulerFacade(getSchedulerEnum());
        return schedulerFacade;
    }


    public static SchedulerEnum getSchedulerEnum(){
        String schedulerType = SchedulerConfig.getString(SchedulerConstant.SCHEDULER_TYPE).toLowerCase();
        return SchedulerEnum.fromKey(schedulerType);
    }


    public static SchedulerEnum getSchedulerEnumFromString(String schedulerType){
        return SchedulerEnum.fromKey(schedulerType.toLowerCase());
    }


}
