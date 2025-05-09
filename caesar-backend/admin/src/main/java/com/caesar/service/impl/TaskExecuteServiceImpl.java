package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.config.PublishConfig;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskExecutePlan;
import com.caesar.entity.CaesarTaskExecuteRecord;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.enums.EnvironmentEnum;
import com.caesar.mapper.DatasourceMapper;
import com.caesar.mapper.TaskExecuteMapper;
import com.caesar.mapper.TaskExecutePlanMapper;
import com.caesar.mapper.TaskMapper;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.Executor;
import com.caesar.service.TaskExecuteService;
import com.caesar.runner.params.TaskInfo;
import com.caesar.shell.ShellTask;
import com.caesar.task.Task;
import com.caesar.util.TaskUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class TaskExecuteServiceImpl extends ServiceImpl<TaskExecuteMapper, CaesarTaskExecuteRecord> implements TaskExecuteService {

    private static final Logger logger = Logger.getLogger(TaskExecuteServiceImpl.class);

    private static final ExecutorService workService = Executors.newFixedThreadPool((Integer) ((Map)new PublishConfig().getCaesarConfig().get("engine")).get("max-parallel-number"));


    @Resource
    TaskExecutePlanMapper taskExecutePlanMapper;

    @Resource
    TaskExecuteMapper taskExecuteMapper;

    @Resource
    TaskMapper taskMapper;

    @Resource
    DatasourceMapper datasourceMapper;


    @Override
    public Boolean saveExecutePlan(CaesarTaskExecutePlan taskExecutePlan) {
        return taskExecutePlanMapper.save(taskExecutePlan);
    }

    @Override
    public Boolean execute(CaesarTaskExecuteRecordDto taskExecuteRecordDto) {

        String parameter = taskExecuteRecordDto.getParameter();
        EnvironmentEnum environment = EnvironmentEnum.fromKey(taskExecuteRecordDto.getEnvironment());

        CaesarTask caesarTask = taskMapper.getTaskInfoFromId(taskExecuteRecordDto.getTaskId());
        TaskInfo task = TaskUtils.generalTaskInfo(taskExecuteRecordDto, caesarTask);

        CaesarTaskExecuteRecord taskExecuteRecord = new CaesarTaskExecuteRecord();
        taskExecuteRecord.setTaskId(caesarTask.getId());
//        taskExecuteRecord.setUuid(UUID.randomUUID().toString().toLowerCase().replaceAll("-",""));
        taskExecuteRecord.setUuid(taskExecuteRecordDto.getUuid());
        taskExecuteRecord.setTaskName(caesarTask.getTaskName());
        taskExecuteRecord.setEnvironment(environment.getKey());
        taskExecuteRecord.setBeginTime(LocalDateTime.now());
        taskExecuteRecord.setIsSuccess(0);
        taskExecuteRecord.setParameter(parameter);

        switch (environment){
            case TEST:
                this.workService.submit(()->{
                    taskExecuteMapper.insert(taskExecuteRecord);
                    int id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
                    taskExecuteRecord.setId(id);
                    try {
                        ExecutionResult<ShellTask> job = Executor.execute(task); // 执行测试任务
                        ShellTask jobTask = job.getData();
                        if(jobTask.getStatus().isSuccess()){
                            taskExecuteRecord.setEndTime(LocalDateTime.now());
                            taskExecuteRecord.setIsSuccess(1);
                        }else {
                            taskExecuteRecord.setEndTime(LocalDateTime.now());
                            taskExecuteRecord.setIsSuccess(0);
                        }
                        jobTask.cancel();
                    }catch (Exception e){
                        taskExecuteRecord.setIsSuccess(0);
                        e.printStackTrace();
                    }
                    taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(),taskExecuteRecord.getEndTime(),taskExecuteRecord.getId());

                });
                break;
            case PRODUCTION:
                boolean isOnline = null != taskMapper.checkOnlineById(taskExecuteRecord.getTaskId())?true:false;
                if(isOnline){
                    this.workService.submit(()->{
                        taskExecuteMapper.insert(taskExecuteRecord);
                        int id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
                        taskExecuteRecord.setId(id);
                        try {
                            ExecutionResult<ShellTask> job = Executor.execute(task); // 执行任务
                            ShellTask jobTask = job.getData();
                            if(jobTask.getStatus().isSuccess()){
                                taskExecuteRecord.setEndTime(LocalDateTime.now());
                                taskExecuteRecord.setIsSuccess(1);
                            }else {
                                taskExecuteRecord.setEndTime(LocalDateTime.now());
                                taskExecuteRecord.setIsSuccess(0);
                            }
                            jobTask.cancel();
                        }catch (Exception e){
                            taskExecuteRecord.setIsSuccess(0);
                            e.printStackTrace();
                        }
                        taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(),taskExecuteRecord.getEndTime(),taskExecuteRecord.getId());
                    });
                }else{
                    logger.info(String.format("不能执行未发版任务"));
                    return false;
                }
                break;
        }

        return true;
    }


    @Override
    public Boolean refresh(List<CaesarTaskExecuteRecordDto> taskExecuteRecordDtos) {

        for(CaesarTaskExecuteRecordDto taskExecuteRecordDto:taskExecuteRecordDtos){
            taskExecutePlanMapper.updatePlanStatus(taskExecuteRecordDto.getPlanUuid(),2);
            Boolean flag = this.execute(taskExecuteRecordDto);
        }

        List<CaesarTaskExecuteRecord> taskExecuteRecords = taskExecuteMapper.findRecordByPlan(taskExecuteRecordDtos.get(0).getPlanUuid());

        boolean flag = true;
        for(CaesarTaskExecuteRecordDto taskExecuteRecordDto:taskExecuteRecordDtos){
            boolean tmpFlag = false;
            for(CaesarTaskExecuteRecord taskExecuteRecord:taskExecuteRecords){
                if(taskExecuteRecordDto.getPlanUuid().equals(taskExecuteRecord.getPlanUuid())){
                    tmpFlag = true;
                    break;
                }
            }
            flag = flag & tmpFlag;
        }

        if(flag){
            taskExecutePlanMapper.updatePlanStatus(taskExecuteRecordDtos.get(0).getPlanUuid(),3);
        }else {
            taskExecutePlanMapper.updatePlanStatus(taskExecuteRecordDtos.get(0).getPlanUuid(),4);
        }

        return flag;
    }

    @Override
    public Boolean validateTaskIsPassedTest(int taskId) {
        return taskExecuteMapper.validateTaskIsPassedTest(taskId);
    }

}
