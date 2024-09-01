package com.caesar.factory;

import com.caesar.enums.SchedulerEnum;
import com.caesar.exception.NotExistsDolphinLevelException;
import com.caesar.scheduler.SchedulerInstance;



public interface SchedulerFactory {

    SchedulerInstance createScheduler() throws NotExistsDolphinLevelException;

    SchedulerEnum getSchedulerType();


}
