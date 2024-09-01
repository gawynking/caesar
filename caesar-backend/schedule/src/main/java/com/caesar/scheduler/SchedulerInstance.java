package com.caesar.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.exception.TaskCodeNotNullException;
import com.caesar.model.SchedulerModel;


public interface SchedulerInstance {

    JSONObject deployTask(SchedulerModel schedulerModel)throws ProjectNotExistsException,GenTaskCodeFaildException,TaskCodeNotNullException;

    JSONObject createTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException;

    JSONObject updateTask(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException, TaskCodeNotNullException;

    JSONObject deleteTask(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException,TaskCodeNotNullException;

    JSONObject release(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException;

    JSONObject timingTask(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException;

    JSONObject deleteTiming(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException;

}
