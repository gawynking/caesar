package com.caesar.scheduler.dolphin;

import com.caesar.config.SchedulerConfig;
import com.caesar.exception.NotExistsDolphinLevelException;
import com.caesar.factory.SchedulerFactory;
import com.caesar.enums.SchedulerEnum;
import com.caesar.factory.SchedulerType;
import com.caesar.scheduler.SchedulerInstance;
import com.caesar.scheduler.dolphin.enums.DolphinLevel;

public class DolphinSchedulerFactory extends SchedulerType implements SchedulerFactory {

    public DolphinSchedulerFactory(){
        super.schedulerType = SchedulerEnum.DOLPHINSCHEDULER;
    }

    @Override
    public SchedulerInstance createScheduler() throws NotExistsDolphinLevelException {
        if(DolphinLevel.PROJECT == DolphinLevel.getByCode(SchedulerConfig.DOLPHIN_LEVEL)){
            return new DolphinSchedulerProjectInstance();
        }else if(DolphinLevel.WORKFLOW == DolphinLevel.getByCode(SchedulerConfig.DOLPHIN_LEVEL)){
            return new DolphinSchedulerWorkflowInstance();
        }
        throw new NotExistsDolphinLevelException("指定了不被支持 Dolphin 调度级别，必须指定 dolphin_level in (project,workflow) 内");
    }

    @Override
    public SchedulerEnum getSchedulerType() {
        return super.schedulerType;
    }

}
