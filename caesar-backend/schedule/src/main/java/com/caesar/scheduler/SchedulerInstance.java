package com.caesar.scheduler;

import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.model.SchedulerModel;

public interface SchedulerInstance {

    void deployTask(SchedulerModel schedulerModel);
    void createTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException;
    void updateTask(String taskId, SchedulerModel schedulerModel);
    void deleteTask(String taskId);
    String queryTask(String taskId);
    void pauseTask(String taskId);
    void resumeTask(String taskId);
    String queryTaskLogs(String taskId);
    void addTaskDependency(String taskId, SchedulerModel schedulerModel);

}
