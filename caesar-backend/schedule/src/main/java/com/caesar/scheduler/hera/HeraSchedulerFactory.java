package com.caesar.scheduler.hera;

import com.caesar.enums.SchedulerEnum;
import com.caesar.factory.SchedulerFactory;
import com.caesar.factory.SchedulerType;
import com.caesar.scheduler.SchedulerInstance;

public class HeraSchedulerFactory extends SchedulerType implements SchedulerFactory {

    public HeraSchedulerFactory(){
        super.schedulerType = SchedulerEnum.HERA;
    }

    @Override
    public SchedulerInstance createScheduler() {
        return null;
    }

    @Override
    public SchedulerEnum getSchedulerType() {
        return super.schedulerType;
    }
}
