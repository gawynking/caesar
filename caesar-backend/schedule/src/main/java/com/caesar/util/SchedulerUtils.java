package com.caesar.util;

import com.caesar.enums.SchedulerEnum;
import com.caesar.scheduler.SchedulerFacade;


public class SchedulerUtils {


    public static SchedulerFacade getScheduler(SchedulerEnum schedulerType){
        SchedulerFacade schedulerFacade = new SchedulerFacade(schedulerType);
        return schedulerFacade;
    }

    public static SchedulerEnum getSchedulerEnumFromString(String schedulerType){
        return SchedulerEnum.fromKey(schedulerType);
    }


}
