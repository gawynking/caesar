package com.caesar.util;

import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.enums.EngineEnum;
import com.caesar.enums.EnvironmentEnum;
import com.caesar.exception.EngineNotDefineException;
import com.caesar.model.code.TemplateUtils;
import com.caesar.model.code.config.TemplateConstants;
import com.caesar.model.code.enums.DatePeriod;
import com.caesar.runner.params.TaskInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class TaskUtils {

    private static final Logger logger = Logger.getLogger(TaskUtils.class.getName());


    public static TaskInfo generalTaskInfo(CaesarTaskExecuteRecordDto taskExecuteRecordDto, CaesarTask caesarTask){

        TaskInfo task = new TaskInfo();
        Map<String, String> taskConfig = new HashMap<>();

        String parameter = taskExecuteRecordDto.getParameter();
        DatePeriod period = DatePeriod.fromKey(taskExecuteRecordDto.getPeriod());
        EnvironmentEnum environment = EnvironmentEnum.fromKey(taskExecuteRecordDto.getEnvironment());

        EngineEnum engine = EngineEnum.fromTag(caesarTask.getEngine());
        String[] taskTags = caesarTask.getTaskName().split("\\.");


        Map<String, String> schedulerParameterByShell = null;
        TemplateUtils.ExecuteScript executeScript = null;
        try {
            schedulerParameterByShell = TemplateUtils.getSchedulerParameterByShell(period);
            executeScript = TemplateUtils.transformSqlTemplate(caesarTask.getTaskScript());
        } catch (EngineNotDefineException e) {
            e.printStackTrace();
            return null;
        }


        if(null == engine || engine != executeScript.getEngine()){
            logger.info(String.format("任务执行失败，模板中定义engine:%s 和系统创建指定engine:%s 不一致",executeScript.getEngine(),engine));
            return null;
        }

        if(taskTags.length<2){
            logger.info(String.format("任务名称(%s)不符合规范",caesarTask.getTaskName()));
            return null;
        }

        if(!(taskTags[0]+"."+taskTags[1]).equals(executeScript.getTaskName())){
            logger.info("任务执行失败，模板中定义任务名称"+executeScript.getTaskName()+"和系统创建指定任务名称"+(taskTags[0]+"."+taskTags[1])+"不一致");
            return null;
        }

        // 参数检查
        Set<String> tmpParameterSet = JSONUtils.getJSONObjectFromString(parameter).keySet();
        Map<String, String> tmpCustomParams = executeScript.getCustomParamValues();
        for(String param: tmpCustomParams.keySet()){
            if(null == tmpCustomParams.get(param) && !tmpParameterSet.contains(param)){
                logger.info(String.format("本次任务传入参数{%s}没有完全覆盖,请检查后重试!",executeScript.getCustomParamValues()));
                return null;
            }
            tmpCustomParams.put(param,schedulerParameterByShell.get(param));
        }

        logger.info("");
        logger.info(String.format("脚本外传参数(shell)为: %s",parameter));
        logger.info(String.format("脚本外传参数(code)为: %s",executeScript.getCustomParamValues()));
        logger.info("");
        logger.info(String.format("本次执行脚本如下: \n%s",executeScript.getScript()));
        logger.info("");


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
        task.setTaskName(String.join(".", Arrays.copyOfRange(taskTags, 1, taskTags.length)));
        task.setEngineParams(executeScript.getEngineParams());
        task.setCustomParamValues(executeScript.getCustomParamValues());
        task.setCode(code);
        task.setEnvironment(environment);
        task.setPeriod(DatePeriod.fromKey(taskExecuteRecordDto.getPeriod()));
        task.setTaskInputParams(JSONUtils.getMapFromJSONObject(JSONUtils.getJSONObjectFromString(parameter)));

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
                return null;
        }

//        String dsConfig = datasourceMapper.findDatasourceInfoByEnvAndEngine(engine.getTag(), datasourceType);
//        taskConfig.putAll(Optional.ofNullable(JSONUtils.getMapFromJSONObject(JSONUtils.getJSONObjectFromString(dsConfig))).orElse(new HashMap()));
        task.setConfig(taskConfig);

        return task;

    }



}
