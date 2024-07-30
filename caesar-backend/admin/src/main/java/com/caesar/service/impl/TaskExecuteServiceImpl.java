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
import com.caesar.model.code.TemplateUtils;
import com.caesar.model.code.model.Pair;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.Executor;
import com.caesar.service.TaskExecuteService;
import com.caesar.params.TaskInfo;
import com.caesar.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class TaskExecuteServiceImpl extends ServiceImpl<TaskExecuteMapper, CaesarTaskExecuteRecord> implements TaskExecuteService {

    private static final Logger logger = Logger.getLogger(TaskExecuteServiceImpl.class);

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
        TaskInfo task = new TaskInfo();
        Map<String, String> taskConfig = new HashMap<>();

        EngineEnum engine = EngineEnum.fromTag(caesarTask.getEngine());
        String[] taskTags = caesarTask.getTaskName().split("\\.");
        TaskContentParser taskContentParser = new TaskContentParser(caesarTask.getTaskScript());
        String script = TemplateUtils.transformSqlTemplate(taskContentParser);
        for(Pair systemParam:taskContentParser.getTaskContentModel().getParamsConfig().getSystemParams()){
            if("system_user".equals(systemParam.getKey())){
                task.setSystemUser(systemParam.getValue());
                break;
            }
        }
        task.setEngine(engine);
        task.setDbLevel(taskTags[0]);
        task.setTaskName(taskTags[1]);
        task.setCode(script);

        logger.info(String.format("Caesar -> Begin Execute Code: \n %s \n\n\n", script));

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
        taskExecuteRecord.setUuid(UUID.randomUUID().toString().toLowerCase().replaceAll("-",""));
        taskExecuteRecord.setTaskName(caesarTask.getTaskName());
        taskExecuteRecord.setEnvironment(environment.getKey());
        taskExecuteRecord.setBeginTime(LocalDateTime.now());
        taskExecuteRecord.setIsSuccess(0);
        taskExecuteRecord.setParameter(taskExecuteRecordDto.getParameter());

        switch (environment){
            case TEST:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        taskExecuteMapper.insert(taskExecuteRecord);
//                        int id = taskExecuteMapper.findIdFromNow(taskExecuteRecord);
                        int id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
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
                boolean isTested = false;
                Integer id = taskExecuteMapper.checkTaskExecuteTest(taskExecuteRecord);
                if(null != id){
                    isTested= true;
                }
                if(isTested){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            taskExecuteMapper.insert(taskExecuteRecord);
//                            int id = taskExecuteMapper.findIdFromNow(taskExecuteRecord);
                            int id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
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
                boolean isTestedAndStaged = null != taskExecuteMapper.checkTaskExecuteTestAndStage(taskExecuteRecord)?true:false;
                boolean isOnline = null != taskMapper.checkOnlineById(taskExecuteRecord.getTaskId())?true:false;
                if(isTestedAndStaged && isOnline){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            taskExecuteMapper.insert(taskExecuteRecord);
//                            int id = taskExecuteMapper.findIdFromNow(taskExecuteRecord);
                            int id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
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

    @Override
    public Boolean refresh(List<CaesarTaskExecuteRecordDto> taskExecuteRecordDtos) {

        for(CaesarTaskExecuteRecordDto taskExecuteRecordDto:taskExecuteRecordDtos){
            this.execute(taskExecuteRecordDto);
        }

        return true;
    }

}
