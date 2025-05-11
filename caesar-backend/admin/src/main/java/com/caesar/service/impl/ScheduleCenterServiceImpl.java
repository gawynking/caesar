package com.caesar.service.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.config.CaesarConfig;
import com.caesar.entity.CaesarScheduleConfig;
import com.caesar.entity.CaesarScheduleDependency;
import com.caesar.entity.dto.CaesarScheduleConfigDto;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.entity.vo.response.ScheduleBaseInfoVo;
import com.caesar.entity.vo.response.ScheduleInfoVo;
import com.caesar.entity.vo.response.TaskDependency;
import com.caesar.enums.EngineEnum;
import com.caesar.exception.CaesarScheduleConfigSyncException;
import com.caesar.exception.EngineNotDefineException;
import com.caesar.exception.SqlParseException;
import com.caesar.mapper.ScheduleConfigMapper;
import com.caesar.mapper.ScheduleDependencyMapper;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.model.ScheduleResponse;
import com.caesar.model.SchedulerModel;
import com.caesar.model.code.TemplateUtils;
import com.caesar.scheduler.SchedulerFacade;
import com.caesar.service.ScheduleCenterService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class ScheduleCenterServiceImpl extends ServiceImpl<ScheduleConfigMapper, CaesarScheduleConfig> implements ScheduleCenterService {

    private static final Logger logger = Logger.getLogger(ScheduleCenterServiceImpl.class.getName());

    @Resource
    ScheduleConfigMapper scheduleConfigMapper;

    @Resource
    ScheduleDependencyMapper scheduleDependencyMapper;

    @Resource
    TaskMapper taskMapper;

    @Resource
    UserMapper userMapper;

    @Autowired
    CaesarConfig caesarConfig;

    private static SchedulerFacade schedulerFacade = SchedulerUtils.getScheduler();



    @Override
    public ScheduleBaseInfoVo getScheduleBaseInfo() {

        String scheduleCategory = caesarConfig.getScheduleCategory();
        String scheduleLevel = caesarConfig.getScheduleLevel();
        String scheduleProject = caesarConfig.getScheduleProject();

        return new ScheduleBaseInfoVo(
                "dolphin".equals(scheduleCategory)?1:2,
                "workflow".equals(scheduleLevel)?1:2,
                scheduleProject
        );

    }

    @Override
    public ScheduleInfoVo getTaskSchedule(String scheduleName) {
        CaesarScheduleConfigDto scheduleConfig = scheduleConfigMapper.getTaskSchedule(scheduleName);
        List<CaesarScheduleDependency> scheduleDependency = Optional.ofNullable(scheduleDependencyMapper.getTaskScheduleDependency(scheduleName)).orElse(new ArrayList<>());

        ScheduleInfoVo scheduleInfoVo = new ScheduleInfoVo();
        List<ScheduleInfoVo.Dependency> dependencys = new ArrayList<>();
        scheduleInfoVo.setScheduleCategory(scheduleConfig.getScheduleCategory());
        scheduleInfoVo.setProject(scheduleConfig.getProject());
        scheduleInfoVo.setScheduleCode(scheduleConfig.getScheduleCode());
        scheduleInfoVo.setScheduleName(scheduleConfig.getScheduleName());
        scheduleInfoVo.setReleaseStatus(scheduleConfig.getReleaseStatus());
        scheduleInfoVo.setTaskType(scheduleConfig.getTaskType());
        scheduleInfoVo.setScheduleParams(scheduleConfig.getScheduleParams());
        scheduleInfoVo.setTaskPriority(scheduleConfig.getTaskPriority());
        scheduleInfoVo.setFailRetryTimes(scheduleConfig.getFailRetryTimes());
        scheduleInfoVo.setFailRetryInterval(scheduleConfig.getFailRetryInterval());
        scheduleInfoVo.setOwnerName(scheduleConfig.getOwnerName());
        scheduleInfoVo.setPeriod(scheduleConfig.getPeriod());
        scheduleInfoVo.setDateValue(scheduleConfig.getDateValue());
        scheduleInfoVo.setVersion(scheduleConfig.getVersion());
        scheduleInfoVo.setDependency(dependencys);

        for(CaesarScheduleDependency dependency:scheduleDependency){
            ScheduleInfoVo.Dependency dependencyItem = new ScheduleInfoVo.Dependency();
            dependencyItem.setPreScheduleCode(dependency.getPreScheduleCode());
            dependencyItem.setPreScheduleName(scheduleConfigMapper.getScheduleNameFromScheduleCode(dependency.getPreScheduleCode()));
            dependencyItem.setJoinType(dependency.getJoinType());
            dependencyItem.setOwnerId(dependency.getOwnerId());
            dependencyItem.setOwnerName(userMapper.getUsernameFromId(dependency.getOwnerId()));
            dependencys.add(dependencyItem);
        }

        return scheduleInfoVo;
    }


    @Override
    public List<ScheduleInfoVo> getTaskSchedules(String taskName) {

        List<CaesarScheduleConfigDto> taskSchedules = scheduleConfigMapper.getTaskSchedules(taskName);
        List<CaesarScheduleDependency> scheduleDependencies = Optional.ofNullable(scheduleDependencyMapper.getTaskScheduleDependencys(taskName)).orElse(new ArrayList<>());

        List<ScheduleInfoVo> scheduleInfoVos = new ArrayList<>();
        for(CaesarScheduleConfigDto scheduleConfig: taskSchedules){
            ScheduleInfoVo scheduleInfoVo = new ScheduleInfoVo();
            List<ScheduleInfoVo.Dependency> dependencys = new ArrayList<>();
            scheduleInfoVo.setScheduleCategory(scheduleConfig.getScheduleCategory());
            scheduleInfoVo.setProject(scheduleConfig.getProject());
            scheduleInfoVo.setScheduleCode(scheduleConfig.getScheduleCode());
            scheduleInfoVo.setScheduleName(scheduleConfig.getScheduleName());
            scheduleInfoVo.setReleaseStatus(scheduleConfig.getReleaseStatus());
            scheduleInfoVo.setTaskType(scheduleConfig.getTaskType());
            scheduleInfoVo.setScheduleParams(scheduleConfig.getScheduleParams());
            scheduleInfoVo.setTaskPriority(scheduleConfig.getTaskPriority());
            scheduleInfoVo.setFailRetryTimes(scheduleConfig.getFailRetryTimes());
            scheduleInfoVo.setFailRetryInterval(scheduleConfig.getFailRetryInterval());
            scheduleInfoVo.setOwnerName(scheduleConfig.getOwnerName());
            scheduleInfoVo.setVersion(scheduleConfig.getVersion());
            scheduleInfoVo.setPeriod(scheduleConfig.getPeriod());
            scheduleInfoVo.setDateValue(scheduleConfig.getDateValue());
            scheduleInfoVo.setDependency(dependencys);
            for(CaesarScheduleDependency dependency:scheduleDependencies){
                if(scheduleConfig.getScheduleCode().equals(dependency.getScheduleCode())){
                    ScheduleInfoVo.Dependency dependencyItem = new ScheduleInfoVo.Dependency();
                    dependencyItem.setPreScheduleCode(dependency.getPreScheduleCode());
                    dependencyItem.setPreScheduleName(scheduleConfigMapper.getScheduleNameFromScheduleCode(dependency.getPreScheduleCode()));
                    dependencyItem.setJoinType(dependency.getJoinType());
                    dependencyItem.setOwnerId(dependency.getOwnerId());
                    dependencyItem.setOwnerName(userMapper.getUsernameFromId(dependency.getOwnerId()));
                    dependencys.add(dependencyItem);
                }
            }
            scheduleInfoVos.add(scheduleInfoVo);
        }

        return scheduleInfoVos;
    }


    @Override
    public List<TaskDependency> getTaskDependencies(String taskName, Integer taskVersion, String period) throws SqlParseException {

        logger.info(String.format("开始解析任务 %s >> %s >> %s 依赖",taskName,taskVersion,period));

        JSONArray allCaesarSchedulers = null;
        try {
            allCaesarSchedulers =  JSONUtils.getJSONArrayFromList(this.syncCaesarSchedulerConfig());
        }catch (Exception e){
            throw new RuntimeException("同步Caesar调度配置失败，请及时介入检查.");
        }


        CaesarTaskVo taskInfo = taskMapper.getCurrentTaskInfoWithVersion(taskName, taskVersion);
        EngineEnum engine = EngineEnum.fromTag(taskInfo.getEngine());
        String rawTaskCode = taskInfo.getTaskScript();

        String code = null;
        TemplateUtils.ExecuteScript executeScript = null;
        try {
            executeScript = TemplateUtils.transformSqlTemplate(rawTaskCode);
            code = executeScript.getCode();
        } catch (EngineNotDefineException e) {
            throw new RuntimeException(e);
        }


        if(null == allCaesarSchedulers || allCaesarSchedulers.size() == 0){
            return new ArrayList<>();
        }


        // 计算返回列表
        List<TaskDependency> effectiveDependencies = new ArrayList<>();
        // 代码引用同时在调度系统存在有效任务才会被认可为是Caesar认可的有效依赖
        for(String sql: code.split(";")){

            if(StringUtils.isEmpty(sql) || StringUtils.isEmpty(sql.replaceAll("\\s",""))){
                continue;
            }

            try {
                List<Map<String, Integer>> tableFromTo = SQLParserUtils.getTableFromTo(sql, SQLParserUtils.CaesarEngineType2DruidDbType(engine));
                Map<String, Integer> fromTables = tableFromTo.get(1);
                for(Map.Entry<String,Integer> entry: fromTables.entrySet()){

                    String depTablename = entry.getKey();
                    List<CaesarScheduleConfigDto> caesarScheduleConfigs = scheduleConfigMapper.findTaskScheduleConfigListFromTaskNameAndPeriod(depTablename,period.toLowerCase());

                    JSONArray curTablenameSchedulers = JSONUtils.getJSONArray();
                    for(int i=0;i<allCaesarSchedulers.size();i++){
                        JSONObject tmpDependency = allCaesarSchedulers.getJSONObject(i);
                        if(tmpDependency.getString("name").toLowerCase().startsWith(depTablename.toLowerCase())){
                            for(CaesarScheduleConfigDto caesarScheduleConfig :caesarScheduleConfigs){
                                if(tmpDependency.getString("code").equals(caesarScheduleConfig.getScheduleCode())){
                                    curTablenameSchedulers.add(tmpDependency);

                                    TaskDependency taskDependency = new TaskDependency();
                                    taskDependency.setDependencyName(caesarScheduleConfig.getScheduleName());
                                    taskDependency.setSchedulerCode(tmpDependency.getString("code"));
                                    taskDependency.setJoinTypeDesc("自动识别");
                                    effectiveDependencies.add(taskDependency);

                                }
                            }
                        }
                    }

                    if(curTablenameSchedulers.size()>0){
                    }else {
                        logger.info(String.format("当前任务 %s 依赖任务 [%s] 未配置调度.",taskName, depTablename));
                    }

                }
            } catch (Exception e) {
            }
        }

        return effectiveDependencies;
    }


    @Override
    public List<CaesarScheduleConfig> syncCaesarSchedulerConfig() throws CaesarScheduleConfigSyncException {

        List<CaesarScheduleConfig> allSchedulerConfig = new ArrayList<>();

        List<CaesarScheduleConfig> allCaesarSystemSchedulers = scheduleConfigMapper.getAllCaesarSchedulerConfig();

        JSONArray allScheduleSystemSchedulers = null;

        ScheduleBaseInfoVo scheduleBaseInfo = getScheduleBaseInfo();
        String project = scheduleBaseInfo.getProject();

        if(scheduleBaseInfo.getScheduleCategory() != 1){
            throw new CaesarScheduleConfigSyncException("当前仅支持DolphinScheduler调度");
        }

        if(scheduleBaseInfo.getScheduleLevel() == 1){ // workflow
            String[] scheduleItems = project.split("___");
            if(scheduleItems.length != 2){
                throw new CaesarScheduleConfigSyncException(String.format("配置调度项目不符合规范，当前调度项目为: %s, 允许项目名称必须符合规范: ${project_name}___${workflow_name}", project));
            }
            allScheduleSystemSchedulers = schedulerFacade.queryTaskList(scheduleItems[0],scheduleItems[1]).getJSONArray("data"); // 获取调度列表
        } else if (scheduleBaseInfo.getScheduleLevel() == 2) { // project
//            allScheduleSystemSchedulers = schedulerFacade.queryTaskList(project,null).getJSONArray("data"); // 获取调度列表
            throw new CaesarScheduleConfigSyncException("当前不支持 跨工作流级别 调度依赖模式(即当前仅支持 工作流定义内-任务级别依赖 模式调度).请修改配置文件'调度模式'为 schedule.level: workflow, 并重启服务.");
        }



        if(OsUtils.isTest()){
            logger.info(String.format("项目 %s 部署调度任务列表: \n\t=> %s",project ,allSchedulerConfig.toString()));
        }

        return allSchedulerConfig;
    }


    @Override
    public Boolean genTaskSchedule(GeneralScheduleInfoVo scheduleInfo) {

        Boolean flag = false;
        CaesarScheduleConfig scheduleConfig = BeanConverterTools.convert(scheduleInfo, CaesarScheduleConfig.class);
        scheduleConfig.setScheduleCode(UUID.randomUUID().toString().replaceAll("-",""));
        scheduleConfig.setVersion(ScheduleVersionUtils.getInstance(scheduleConfigMapper).getVersion());
        scheduleConfig.setOwnerId(userMapper.getUserIdFromUserName(scheduleInfo.getOwnerName()));
        Boolean insertTaskScheduleFlag = scheduleConfigMapper.genTaskSchedule(scheduleConfig);

        flag = insertTaskScheduleFlag;
        String scheduleCode = scheduleConfig.getScheduleCode();
        for(GeneralScheduleInfoVo.Dependency dependency:scheduleInfo.getDependency()){
            dependency.setPreScheduleCode(scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(dependency.getPreScheduleName()));
            Boolean insertTaskDependencyFlag = updateTaskScheduleDenpendency(scheduleCode,dependency);
            if(!insertTaskDependencyFlag){
                flag = false;
            }
        }

        String rawTaskCode = taskMapper.getCurrentTaskInfoWithVersion(scheduleInfo.getTaskName(),scheduleInfo.getTaskVersion()).getTaskScript();
        String taskCode = null;
        TemplateUtils.ExecuteScript executeScript = null;
        try {
            executeScript = TemplateUtils.transformSqlTemplate(rawTaskCode);
            taskCode = executeScript.getScript();
        } catch (EngineNotDefineException e) {
            throw new RuntimeException(e);
        }
        scheduleInfo.setTaskCode(taskCode);

        logger.info("本次创建任务执行脚本如下: " + taskCode);

        SchedulerFacade schedulerClient = CaesarScheduleUtils.getSchedulerClient();
        SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfig(scheduleInfo);
        ScheduleResponse scheduleResponse = schedulerClient.deployTask(schedulerModel);

        logger.log(Level.INFO,"任务(0)创建(1),结果(2).",new String[]{scheduleInfo.getScheduleName(),scheduleResponse.getCode().toString(),scheduleResponse.getData().toString()});

        return flag;

    }


    @Override
    public Boolean updateTaskSchedule(GeneralScheduleInfoVo scheduleInfo) {
        Boolean flag = false;
//        String scheduleCode = scheduleInfo.getScheduleCode();
        String scheduleCode = scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(scheduleInfo.getScheduleName());
        CaesarScheduleConfig scheduleConfig = BeanConverterTools.convert(scheduleInfo, CaesarScheduleConfig.class);
        scheduleConfig.setScheduleCode(scheduleCode);
        scheduleConfig.setVersion(ScheduleVersionUtils.getInstance(scheduleConfigMapper).getVersion());
        scheduleConfig.setOwnerId(userMapper.getUserIdFromUserName(scheduleInfo.getOwnerName()));
        Boolean updateTaskScheduleFlag = scheduleConfigMapper.updateTaskSchedule(scheduleConfig);

        flag = updateTaskScheduleFlag;
        for(GeneralScheduleInfoVo.Dependency dependency:scheduleInfo.getDependency()){
            dependency.setPreScheduleName(scheduleConfigMapper.getScheduleNameFromScheduleCode(dependency.getPreScheduleCode()));
//            dependency.setPreScheduleCode(scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(dependency.getPreScheduleName()));
            Boolean insertTaskDependencyFlag = updateTaskScheduleDenpendency(scheduleCode,dependency);
            if(!insertTaskDependencyFlag){
                flag = false;
            }
        }

        String rawTaskCode = taskMapper.getCurrentTaskInfoWithVersion(scheduleInfo.getTaskName(),scheduleInfo.getTaskVersion()).getTaskScript();
        String taskCode = null;
        TemplateUtils.ExecuteScript executeScript = null;
        try {
            executeScript = TemplateUtils.transformSqlTemplate(rawTaskCode);
            taskCode = executeScript.getScript();
        } catch (EngineNotDefineException e) {
            throw new RuntimeException(e);
        }
        scheduleInfo.setTaskCode(taskCode);

        logger.info("本次创更新务执行脚本如下: " + taskCode);

        SchedulerFacade schedulerClient = CaesarScheduleUtils.getSchedulerClient();
        SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfig(scheduleInfo);
        ScheduleResponse scheduleResponse = schedulerClient.deployTask(schedulerModel);

        logger.log(Level.INFO,"任务(0)更新(1),结果(2).",new String[]{scheduleInfo.getScheduleName(),scheduleResponse.getCode().toString(),scheduleResponse.getData().toString()});

        return flag;
    }


    @Override
    public Boolean deleteTaskSchedule(String scheduleName) {
        Boolean flag = false;
        String scheduleCode = scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(scheduleName);
        Boolean flag1 = scheduleDependencyMapper.deleteByScheduleCode(scheduleCode);
        Boolean flag2 = scheduleConfigMapper.deleteByScheduleCode(scheduleCode);
        flag = flag1 & flag2;

        SchedulerModel scheduleModel = CaesarScheduleUtils.getDeleteScheduleModel(scheduleName);
        SchedulerFacade schedulerClient = CaesarScheduleUtils.getSchedulerClient();
        ScheduleResponse scheduleResponse = schedulerClient.deleteTask(scheduleModel);
        logger.info(String.format("删除调度任务(%s),Code (%s);结果 (%s)",scheduleName,scheduleResponse.getCode(),scheduleResponse.getData()));

        return flag;
    }


    @Override
    public Boolean updateTaskScheduleDenpendency(String scheduleCode, GeneralScheduleInfoVo.Dependency dependency) {
        CaesarScheduleDependency caesarScheduleDependency = scheduleDependencyMapper.findTaskDependency(scheduleCode,dependency.getPreScheduleCode());

        Boolean flag = false;
        if(null == caesarScheduleDependency) {
            CaesarScheduleDependency scheduleDependency = BeanConverterTools.convert(dependency, CaesarScheduleDependency.class);
            scheduleDependency.setScheduleCode(scheduleCode);
            scheduleDependency.setOwnerId(userMapper.getUserIdFromUserName(dependency.getOwnerName()));
            flag = scheduleDependencyMapper.updateTaskDependency(scheduleDependency);
        }

        return flag;
    }

}
