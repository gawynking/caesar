package com.caesar.scheduler.hera;


import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerInstance;

public class HeraSchedulerInstance implements SchedulerInstance {

    @Override
    public void deployTask(SchedulerModel schedulerModel) {
    }

    @Override
    public void createTask(SchedulerModel schedulerModel) {
    }

    @Override
    public void updateTask(String taskId, SchedulerModel schedulerModel) {
    }

    @Override
    public void deleteTask(String taskId) {
    }

    @Override
    public String queryTask(String taskId) {
        return null;
    }

    @Override
    public void pauseTask(String taskId) {
    }

    @Override
    public void resumeTask(String taskId) {
    }

    @Override
    public String queryTaskLogs(String taskId) {
        return null;
    }

    @Override
    public void addTaskDependency(String taskId, SchedulerModel schedulerModel) {
    }

}
