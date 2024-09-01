package com.caesar.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.NotExistsDolphinLevelException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.exception.TaskCodeNotNullException;
import com.caesar.factory.SchedulerFactory;
import com.caesar.factory.SchedulerFactoryManager;
import com.caesar.enums.SchedulerEnum;
import com.caesar.model.ScheduleResponse;
import com.caesar.model.SchedulerModel;


public class SchedulerFacade {

    private static volatile SchedulerFactoryManager schedulerFactoryManager = null;

    static {
        schedulerFactoryManager = new SchedulerFactoryManager();
    }

    private SchedulerFactory schedulerFactory;


    public SchedulerFacade(SchedulerEnum type) {
        this.schedulerFactory = schedulerFactoryManager.getEngineFactory(type);
    }



    public ScheduleResponse deployTask(SchedulerModel schedulerModel) {
        SchedulerInstance instance = null;
        try {
            instance = schedulerFactory.createScheduler();
        } catch (NotExistsDolphinLevelException e) {
            e.printStackTrace();
        }
        try {
            JSONObject result = instance.deployTask(schedulerModel);
            return new ScheduleResponse().success("deployTask",result);
        } catch (ProjectNotExistsException e) {
            e.printStackTrace();
        } catch (GenTaskCodeFaildException e) {
            e.printStackTrace();
        } catch (TaskCodeNotNullException e) {
            e.printStackTrace();
        }
        return new ScheduleResponse().faild("deployTask");
    }

    public ScheduleResponse createTask(SchedulerModel schedulerModel) {
        SchedulerInstance instance = null;
        try {
            instance = schedulerFactory.createScheduler();
        } catch (NotExistsDolphinLevelException e) {
            e.printStackTrace();
        }
        try {
            JSONObject result = instance.createTask(schedulerModel);
            return new ScheduleResponse().success("createTask",result);
        } catch (ProjectNotExistsException e) {
            e.printStackTrace();
        } catch (GenTaskCodeFaildException e) {
            e.printStackTrace();
        }
        return new ScheduleResponse().faild("createTask");
    }

    public ScheduleResponse updateTask(String taskId, SchedulerModel schedulerModel) {
        SchedulerInstance instance = null;
        try {
            instance = schedulerFactory.createScheduler();
        } catch (NotExistsDolphinLevelException e) {
            e.printStackTrace();
        }
        try {
            JSONObject result = instance.updateTask(schedulerModel);
            return new ScheduleResponse().success("updateTask",result);
        } catch (ProjectNotExistsException e) {
            e.printStackTrace();
        } catch (GenTaskCodeFaildException e) {
            e.printStackTrace();
        } catch (TaskCodeNotNullException e) {
            e.printStackTrace();
        }
        return new ScheduleResponse().faild("updateTask");
    }


    public ScheduleResponse deleteTask(SchedulerModel schedulerModel) {
        SchedulerInstance instance = null;
        try {
            instance = schedulerFactory.createScheduler();
        } catch (NotExistsDolphinLevelException e) {
            e.printStackTrace();
        }
        try {
            JSONObject result = instance.deleteTask(schedulerModel);
            return new ScheduleResponse().success("deleteTask",result);
        } catch (ProjectNotExistsException e) {
            e.printStackTrace();
        } catch (GenTaskCodeFaildException e) {
            e.printStackTrace();
        } catch (TaskCodeNotNullException e) {
            e.printStackTrace();
        }
        return new ScheduleResponse().faild("deleteTask");
    }

    public ScheduleResponse release(SchedulerModel schedulerModel) {
        SchedulerInstance instance = null;
        try {
            instance = schedulerFactory.createScheduler();
        } catch (NotExistsDolphinLevelException e) {
            e.printStackTrace();
        }
        try {
            JSONObject result = instance.release(schedulerModel);
            return new ScheduleResponse().success("release",result);
        } catch (ProjectNotExistsException e) {
            e.printStackTrace();
        } catch (GenTaskCodeFaildException e) {
            e.printStackTrace();
        }
        return new ScheduleResponse().faild("release");
    }

}
