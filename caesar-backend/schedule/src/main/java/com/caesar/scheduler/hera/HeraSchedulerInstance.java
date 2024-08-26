package com.caesar.scheduler.hera;


import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerInstance;

public class HeraSchedulerInstance implements SchedulerInstance {

    @Override
    public void deployTask(SchedulerModel schedulerModel) {
    }

    @Override
    public String createTask(SchedulerModel schedulerModel) {
        return null;
    }

    @Override
    public String updateTask(SchedulerModel schedulerModel) {
        return null;
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
