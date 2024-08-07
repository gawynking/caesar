package com.caesar.scheduler;

import com.caesar.factory.SchedulerFactory;
import com.caesar.factory.SchedulerFactoryManager;
import com.caesar.enums.SchedulerEnum;
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



    public void deployTask(SchedulerModel schedulerModel) {
        SchedulerInstance instance = schedulerFactory.createScheduler();
        instance.deployTask(schedulerModel);
    }

    public void createTask(SchedulerModel schedulerModel) {
        SchedulerInstance instance = schedulerFactory.createScheduler();
        instance.createTask(schedulerModel);
    }

    public void updateTask(String taskId, SchedulerModel schedulerModel) {
        SchedulerInstance instance = schedulerFactory.createScheduler();
        instance.updateTask(taskId, schedulerModel);
    }

    public void deleteTask(String taskId) {
        SchedulerInstance instance = schedulerFactory.createScheduler();
        instance.deleteTask(taskId);
    }

    public String queryTask(String taskId) {
        SchedulerInstance instance = schedulerFactory.createScheduler();
        return instance.queryTask(taskId);
    }

    public String queryTaskLogs(String taskId) {
        SchedulerInstance instance = schedulerFactory.createScheduler();
        return instance.queryTaskLogs(taskId);
    }

    public void addTaskDependency(String taskId, SchedulerModel schedulerModel) {
        SchedulerInstance instance = schedulerFactory.createScheduler();
        instance.addTaskDependency(taskId, schedulerModel);
    }

    public void pauseTask(String taskId) {
    }

    public void resumeTask(String taskId) {
    }

}
