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
import com.caesar.util.SchedulerUtils;


public class SchedulerFacade {

    private static volatile SchedulerFactoryManager schedulerFactoryManager = null;

    static {
        schedulerFactoryManager = new SchedulerFactoryManager();
    }

    private SchedulerFactory schedulerFactory;


    public SchedulerFacade(SchedulerEnum type) {
        if(null == type){
            type = SchedulerUtils.getSchedulerEnum();
        }
        this.schedulerFactory = schedulerFactoryManager.getEngineFactory(type);
    }



    public synchronized ScheduleResponse deployTask(SchedulerModel schedulerModel) {
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

    public synchronized ScheduleResponse createTask(SchedulerModel schedulerModel) {
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

    public synchronized ScheduleResponse updateTask(SchedulerModel schedulerModel) {
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


    public synchronized ScheduleResponse deleteTask(SchedulerModel schedulerModel) {
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

    public synchronized ScheduleResponse release(SchedulerModel schedulerModel) {
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



    @Deprecated
    public synchronized JSONObject queryTaskList(String project,String workFlow) {
        SchedulerInstance instance = null;
        try {
            instance = schedulerFactory.createScheduler();
        } catch (NotExistsDolphinLevelException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = instance.queryTaskList(project, workFlow);
        } catch (ProjectNotExistsException e) {
            throw new RuntimeException(e);
        } catch (GenTaskCodeFaildException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }


    public synchronized JSONObject queryProcessTaskList(String project,String workFlow) {
        SchedulerInstance instance = null;
        try {
            instance = schedulerFactory.createScheduler();
        } catch (NotExistsDolphinLevelException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = instance.queryProcessTaskList(project, workFlow);
        } catch (ProjectNotExistsException e) {
            throw new RuntimeException(e);
        } catch (GenTaskCodeFaildException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }
}
