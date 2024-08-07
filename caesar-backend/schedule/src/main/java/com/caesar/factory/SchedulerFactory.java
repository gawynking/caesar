package com.caesar.factory;

import com.caesar.enums.SchedulerEnum;
import com.caesar.scheduler.SchedulerInstance;



public interface SchedulerFactory {

    SchedulerInstance createScheduler();

    SchedulerEnum getSchedulerType();


}
