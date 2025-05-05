package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskExecutePlan;
import com.caesar.entity.CaesarTaskExecuteRecord;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.enums.EngineEnum;
import com.caesar.enums.EnvironmentEnum;
import com.caesar.exception.EngineNotDefineException;
import com.caesar.mapper.DatasourceMapper;
import com.caesar.mapper.TaskExecuteMapper;
import com.caesar.mapper.TaskExecutePlanMapper;
import com.caesar.mapper.TaskMapper;
import com.caesar.model.code.TemplateUtils;
import com.caesar.model.code.config.TemplateConstants;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.Executor;
import com.caesar.service.TaskExecuteService;
import com.caesar.params.TaskInfo;
import com.caesar.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class TaskExecuteServiceImpl extends ServiceImpl<TaskExecuteMapper, CaesarTaskExecuteRecord> implements TaskExecuteService {

    private static final Logger logger = Logger.getLogger(TaskExecuteServiceImpl.class);

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

        TaskInfo task = new TaskInfo();
        Map<String, String> taskConfig = new HashMap<>();

        String parameter = taskExecuteRecordDto.getParameter();
        EnvironmentEnum environment = EnvironmentEnum.fromKey(taskExecuteRecordDto.getEnvironment());
        CaesarTask caesarTask = taskMapper.getTaskInfoFromId(taskExecuteRecordDto.getTaskId());

        EngineEnum engine = EngineEnum.fromTag(caesarTask.getEngine());
        String[] taskTags = caesarTask.getTaskName().split("\\.");


        TemplateUtils.ExecuteScript executeScript = null;
        try {
            executeScript = TemplateUtils.transformSqlTemplate(caesarTask.getTaskScript());
        } catch (EngineNotDefineException e) {
            e.printStackTrace();
            return false;
        }

        logger.info(String.format("本次执行脚本如下: \n%s",executeScript.getScript()));

        if(null == engine || engine != executeScript.getEngine()){
            logger.info(String.format("任务执行失败，模板中定义engine:%s 和系统创建指定engine:%s 不一致",executeScript.getEngine(),engine));
            return false;
        }

        if(taskTags.length<2){
            logger.info(String.format("任务名称(%s)不符合规范",caesarTask.getTaskName()));
            return false;
        }

        if(!(taskTags[0]+"."+taskTags[1]).equals(executeScript.getTaskName())){
            logger.info("任务执行失败，模板中定义任务名称"+executeScript.getTaskName()+"和系统创建指定任务名称"+(taskTags[0]+"."+taskTags[1])+"不一致");
            return false;
        }

        String code = executeScript.getScript();
        Map<String, String> systemParams = executeScript.getSystemParams();
        if(null != systemParams) {
            for (String systemParam : systemParams.keySet()) {
                if (TemplateConstants.CAESAR_SYSTEM_USER.equals(systemParam)) {
                    task.setSystemUser(systemParams.get(systemParam));
                    break;
                }
            }
        }
        task.setEngine(engine);
        task.setDbLevel(taskTags[0]);
        task.setTaskName(String.join(".",Arrays.copyOfRange(taskTags, 1, taskTags.length)));
        task.setEngineParams(executeScript.getEngineParams());
        task.setCustomParamValues(executeScript.getCustomParamValues());
        task.setCode(code);
        task.setTaskParams(JSONUtils.getMapFromJSONObject(JSONUtils.getJSONObjectFromString(parameter)));

        int datasourceType = -1;
        switch (environment){
            case TEST:
                datasourceType=1;
                break;
            case PRODUCTION:
                datasourceType=3;
                break;
            default:
                logger.info("执行环境不匹配，请检查配置信息");
                return false;
        }

        String dsConfig = datasourceMapper.findDatasourceInfoByEnvAndEngine(engine.getTag(), datasourceType);
        taskConfig.putAll(JSONUtils.getMapFromJSONObject(JSONUtils.getJSONObjectFromString(dsConfig)));
        task.setConfig(taskConfig);


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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        taskExecuteMapper.insert(taskExecuteRecord);
                        int id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
                        taskExecuteRecord.setId(id);
                        try {
                            ExecutionResult execute = Executor.execute(task); // 执行测试任务
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
            case PRODUCTION:
                boolean isOnline = null != taskMapper.checkOnlineById(taskExecuteRecord.getTaskId())?true:false;
                if(isOnline){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            taskExecuteMapper.insert(taskExecuteRecord);
                            int id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
                            taskExecuteRecord.setId(id);
                            try {
                                ExecutionResult execute = Executor.execute(task); // 执行任务
                                taskExecuteRecord.setEndTime(LocalDateTime.now());
                                taskExecuteRecord.setIsSuccess(1);
                            }catch (Exception e){
                                taskExecuteRecord.setIsSuccess(0);
                                e.printStackTrace();
                            }
                            taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(),taskExecuteRecord.getEndTime(),taskExecuteRecord.getId());
                        }
                    }).start();
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
