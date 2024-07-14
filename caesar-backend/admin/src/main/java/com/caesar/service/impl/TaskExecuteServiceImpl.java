package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskExecuteRecord;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.enums.EngineEnum;
import com.caesar.enums.Environment;
import com.caesar.mapper.DatasourceMapper;
import com.caesar.mapper.TaskExecuteMapper;
import com.caesar.mapper.TaskMapper;
import com.caesar.model.code.TaskContentParser;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.Executor;
import com.caesar.service.TaskExecuteService;
import com.caesar.task.Task;
import com.caesar.util.JSONUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class TaskExecuteServiceImpl extends ServiceImpl<TaskExecuteMapper, CaesarTaskExecuteRecord> implements TaskExecuteService {

    @Resource
    TaskExecuteMapper taskExecuteMapper;

    @Resource
    TaskMapper taskMapper;

    @Resource
    DatasourceMapper datasourceMapper;

    @Override
    public Boolean execute(CaesarTaskExecuteRecordDto taskExecuteRecordDto) {

        Environment environment = Environment.fromKey(taskExecuteRecordDto.getEnvironment());
        CaesarTask caesarTask = taskMapper.getTaskInfoFromId(taskExecuteRecordDto.getTaskId());
        Task task = new Task();
        Map<String, String> taskConfig = new HashMap<>();


        EngineEnum engine = EngineEnum.fromTag(caesarTask.getEngine());
        String[] taskTags = caesarTask.getTaskName().split("\\.");
        TaskContentParser taskContentParser = new TaskContentParser(caesarTask.getTaskScript());
        task.setEngine(engine);
        task.setDbLevel(taskTags[0]);
        task.setTaskName(taskTags[1]);
        task.setCode(taskContentParser.generateExecuteScript());

        int datasourceType = -1;
        switch (environment){
            case TEST:
                datasourceType=1;
                break;
            case STAGING:
                datasourceType=2;
                break;
            case PRODUCTION:
                datasourceType=3;
                break;
        }

        String dsConfig = datasourceMapper.findDatasourceInfoByEnvAndEngine(engine.getTag(),datasourceType);
        taskConfig.putAll(JSONUtils.getMapFromJSONObject(JSONUtils.getJSONObjectFromString(dsConfig)));
        task.setConfig(taskConfig);

        CaesarTaskExecuteRecord taskExecuteRecord = new CaesarTaskExecuteRecord();
        taskExecuteRecord.setTaskId(caesarTask.getId());
        taskExecuteRecord.setTaskName(caesarTask.getTaskName());
        taskExecuteRecord.setEnvironment(environment.getKey());
        taskExecuteRecord.setBeginTime(LocalDateTime.now());
        taskExecuteRecord.setIsSuccess(0);

        switch (environment){
            case TEST:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        taskExecuteMapper.insert(taskExecuteRecord);
                        int id = taskExecuteMapper.findIdFromNow(taskExecuteRecord);
                        taskExecuteRecord.setId(id);
                        try {
                            ExecutionResult execute = Executor.execute(task);
                            taskExecuteRecord.setEndTime(LocalDateTime.now());
                            taskExecuteRecord.setIsSuccess(1);
                        }catch (Exception e){
                            taskExecuteRecord.setIsSuccess(0);
                            e.printStackTrace();
                        }
                        taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(),taskExecuteRecord.getEndTime(),taskExecuteRecord.getId());
                    }
                }).start();
                break;
            case STAGING:
                boolean isTested = taskExecuteMapper.checkTaskExecuteTest(taskExecuteRecord);
                if(isTested){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            taskExecuteMapper.insert(taskExecuteRecord);
                            int id = taskExecuteMapper.findIdFromNow(taskExecuteRecord);
                            taskExecuteRecord.setId(id);
                            try {
                                ExecutionResult execute = Executor.execute(task);
                                taskExecuteRecord.setEndTime(LocalDateTime.now());
                                taskExecuteRecord.setIsSuccess(1);
                            }catch (Exception e){
                                taskExecuteRecord.setIsSuccess(0);
                                e.printStackTrace();
                            }
                            taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(),taskExecuteRecord.getEndTime(),taskExecuteRecord.getId());
                        }
                    }).start();
                }
                break;
            case PRODUCTION:
                boolean isTestedAndStaged = taskExecuteMapper.checkTaskExecuteTestAndStage(taskExecuteRecord);
                boolean isOnline = taskMapper.checkOnlineById(taskExecuteRecord.getTaskId());
                if(isTestedAndStaged && isOnline){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            taskExecuteMapper.insert(taskExecuteRecord);
                            int id = taskExecuteMapper.findIdFromNow(taskExecuteRecord);
                            taskExecuteRecord.setId(id);
                            try {
                                ExecutionResult execute = Executor.execute(task);
                                taskExecuteRecord.setEndTime(LocalDateTime.now());
                                taskExecuteRecord.setIsSuccess(1);
                            }catch (Exception e){
                                taskExecuteRecord.setIsSuccess(0);
                                e.printStackTrace();
                            }
                            taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(),taskExecuteRecord.getEndTime(),taskExecuteRecord.getId());
                        }
                    }).start();
                }
                break;
        }

        return true;
    }

}
