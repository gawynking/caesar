package com.caesar.scheduler.dolphin;

import com.caesar.factory.SchedulerFactory;
import com.caesar.enums.SchedulerEnum;
import com.caesar.factory.SchedulerType;
import com.caesar.scheduler.SchedulerInstance;

public class DolphinSchedulerFactory extends SchedulerType implements SchedulerFactory {

    public DolphinSchedulerFactory(){
        super.schedulerType = SchedulerEnum.DOLPHINSCHEDULER;
    }

    @Override
    public SchedulerInstance createScheduler() {
        return new DolphinSchedulerInstance();
    }

    @Override
    public SchedulerEnum getSchedulerType() {
        return super.schedulerType;
    }

}
