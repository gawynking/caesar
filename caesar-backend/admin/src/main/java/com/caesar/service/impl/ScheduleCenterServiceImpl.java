package com.caesar.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.config.CaesarConfig;
import com.caesar.entity.CaesarScheduleConfig;
import com.caesar.entity.CaesarScheduleDependency;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.bo.CaesarScheduleConfigInfoBo;
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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


    private static SchedulerFacade schedulerFacade = CaesarScheduleUtils.getSchedulerClient();



    private ScheduledExecutorService syncExecutor;
    private volatile boolean isSyncRunning = false;

    @PostConstruct
    public void init() {
        if(caesarConfig.getSyncMode().equals("interval")){
            syncExecutor = Executors.newScheduledThreadPool(1);
            syncExecutor.scheduleAtFixedRate(this::syncSchedulerConfigWrapper, 1, 30, TimeUnit.MINUTES);
        }
    }

    @PreDestroy
    public void destroy() {
        if (syncExecutor != null) {
            syncExecutor.shutdown();
            try {
                if (!syncExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    syncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                syncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void syncSchedulerConfigWrapper() {
        if (isSyncRunning) {
            logger.warning("上一次同步任务仍在执行，跳过本次执行");
            return;
        }

        try {
            isSyncRunning = true;
            logger.info("开始执行调度配置同步任务...");
            this.syncCaesarSchedulerConfig();
            logger.info("调度配置同步任务完成");
        } catch (Exception e) {
            logger.severe("调度配置同步任务执行失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            isSyncRunning = false;
        }
    }




    @Override
    public ScheduleBaseInfoVo getScheduleBaseInfo() {

        String scheduleCategory = caesarConfig.getScheduleCategory();
        String scheduleLevel = caesarConfig.getScheduleLevel();
        Map<String,String> scheduleProjects = new HashMap<>();
        scheduleProjects.put("hour",caesarConfig.getScheduleProjectHour());
        scheduleProjects.put("day",caesarConfig.getScheduleProjectDay());
        scheduleProjects.put("week",caesarConfig.getScheduleProjectWeek());
        scheduleProjects.put("month",caesarConfig.getScheduleProjectMonth());

        return new ScheduleBaseInfoVo(
                "dolphin".equals(scheduleCategory)?1:2,
                "workflow".equals(scheduleLevel)?1:2,
                scheduleProjects
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
            scheduleInfoVo.setIsOnline(scheduleConfig.getReleaseStatus()==1?true:false);
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
        if(caesarConfig.getSyncMode().equals("created")) {
            try {
                allCaesarSchedulers = JSONUtils.getJSONArrayFromList(this.syncCaesarSchedulerConfig());
            } catch (Exception e) {
                throw new RuntimeException("同步Caesar调度配置失败，请及时介入检查.");
            }
        }else {
            allCaesarSchedulers = JSONUtils.getJSONArrayFromList(scheduleConfigMapper.getAllOnlineCaesarSystemSchedulerConfigs());
        }

        CaesarTaskVo taskInfo = taskMapper.getCurrentTaskInfoWithVersion(taskName, taskVersion);
        EngineEnum engine = EngineEnum.fromTag(taskInfo.getEngine());
        String rawTaskCode = taskInfo.getTaskScript();

        String code = null;
        TemplateUtils.ExecuteScript executeScript = null;
        try {
            executeScript = TemplateUtils.transformSqlTemplate(rawTaskCode);
            code = executeScript.getScript();
        } catch (EngineNotDefineException e) {
            throw new RuntimeException(e);
        }


        if(null == allCaesarSchedulers || allCaesarSchedulers.size() == 0){
            return new ArrayList<>();
        }

        Map<String, String> scheduleCodeSet = new HashMap<>();
        List<CaesarScheduleConfig> selfScheduleConfigs = scheduleConfigMapper.getScheduleConfigsByTaskId(taskInfo.getId());
        for(CaesarScheduleConfig selfScheduleConfig :selfScheduleConfigs){
            scheduleCodeSet.put(selfScheduleConfig.getScheduleCode(), null);
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
                    List<CaesarScheduleConfigDto> caesarScheduleConfigs = Optional.ofNullable(scheduleConfigMapper.findTaskScheduleConfigListFromTaskNameAndPeriod(depTablename+"%",period.toLowerCase())).orElse(new ArrayList<>());

                    for(CaesarScheduleConfigDto caesarScheduleConfig :caesarScheduleConfigs){
                        if(!scheduleCodeSet.containsKey(caesarScheduleConfig.getScheduleCode())) {
                            TaskDependency taskDependency = new TaskDependency();
                            taskDependency.setDependencyName(caesarScheduleConfig.getScheduleName());
                            taskDependency.setSchedulerCode(caesarScheduleConfig.getScheduleCode());
                            taskDependency.setJoinTypeDesc("自动识别");
                            effectiveDependencies.add(taskDependency);
                        }
                    }

                }
            } catch (Exception e) {
            }
        }

        return effectiveDependencies;
    }


    @Override
    public synchronized List<CaesarScheduleConfigInfoBo> syncCaesarSchedulerConfig() throws CaesarScheduleConfigSyncException {

        List<CaesarScheduleConfigInfoBo> allSchedulerConfig = new ArrayList<>();
        List<CaesarScheduleConfigInfoBo> allCaesarSystemSchedulerConfigs = scheduleConfigMapper.getAllCaesarSystemSchedulerConfigs();

        ScheduleBaseInfoVo scheduleBaseInfo = getScheduleBaseInfo();
        Map<String, String> projects = scheduleBaseInfo.getProjects();
        for(String period: projects.keySet()){

            JSONObject processTaskDefine = null;

            String project = projects.get(period);
            if(scheduleBaseInfo.getScheduleCategory() != 1){
                throw new CaesarScheduleConfigSyncException("当前仅支持DolphinScheduler调度");
            }

            if(scheduleBaseInfo.getScheduleLevel() == 1){ // workflow
                String[] scheduleItems = project.split("___");
                if(scheduleItems.length != 2){
                    throw new CaesarScheduleConfigSyncException(String.format("配置调度项目不符合规范，当前调度项目为: %s, 允许项目名称必须符合规范: ${project_name}___${workflow_name}", project));
                }
                processTaskDefine = schedulerFacade.queryProcessTaskList(scheduleItems[0], scheduleItems[1]);
                if(!processTaskDefine.getBoolean("success")){
                    throw new CaesarScheduleConfigSyncException("获取调度配置信息失败.");
                }
            } else if (scheduleBaseInfo.getScheduleLevel() == 2) { // project
                throw new CaesarScheduleConfigSyncException("当前不支持 跨工作流级别 调度依赖模式(即当前仅支持 工作流定义内-任务级别依赖 模式调度).请修改配置文件'调度模式'为 schedule.level: workflow, 并重启服务.");
            }


            // 分别获取Caesar系统所有调度配置和调度系统所有调度配置
            List<CaesarScheduleConfigInfoBo> allCaesarSystemSchedulerConfigByPeriod = new ArrayList<>();
            for(CaesarScheduleConfigInfoBo caesarSystemSchedulerConfig :allCaesarSystemSchedulerConfigs){
                if(period.equals(caesarSystemSchedulerConfig.getPeriod())){ // 处理同周期调度配置
                    allCaesarSystemSchedulerConfigByPeriod.add(caesarSystemSchedulerConfig);
                }
            }
            List<CaesarScheduleConfigInfoBo> allSchedulerSystemSchedulerConfigs = new ArrayList<>(); // 这里list存储的CaesarScheduleConfigInfoBo对象元素并不齐全
            if(scheduleBaseInfo.getScheduleCategory() == 1) {
                allSchedulerSystemSchedulerConfigs = CaesarScheduleUtils.parseDolphinSchedulerSchedulers(processTaskDefine);
            }else {
                throw new RuntimeException();
            }


            HashMap<String, List<CaesarScheduleConfigInfoBo>> diffScheduleConfigCache = new HashMap<>();
            diffScheduleConfigCache.put("caesar",new ArrayList<>());
            diffScheduleConfigCache.put("schedule",new ArrayList<>());
            for(CaesarScheduleConfigInfoBo caesarSystemSchedulerConfig :allCaesarSystemSchedulerConfigByPeriod){
                boolean equal = false;
                for(CaesarScheduleConfigInfoBo schedulerSystemSchedulerConfig :allSchedulerSystemSchedulerConfigs){
                    if(caesarSystemSchedulerConfig.getScheduleName().equals(schedulerSystemSchedulerConfig.getScheduleName())){ // 调度名称匹配
                        if(caesarSystemSchedulerConfig.getReleaseStatus() == schedulerSystemSchedulerConfig.getReleaseStatus()){ // 状态一致
                            equal = true;
                            break;
                        }
                    }
                }
                if(!equal){ // 说明Caesar系统调度没有正确同步到外部调度系统
                    diffScheduleConfigCache.get("caesar").add(caesarSystemSchedulerConfig);
                }
            }
            for(CaesarScheduleConfigInfoBo schedulerSystemSchedulerConfig :allSchedulerSystemSchedulerConfigs){
                boolean equal = false;
                for(CaesarScheduleConfigInfoBo caesarSystemSchedulerConfig :allCaesarSystemSchedulerConfigByPeriod){
                    if(caesarSystemSchedulerConfig.getScheduleName().equals(schedulerSystemSchedulerConfig.getScheduleName())){ // 调度名称匹配
                        if(caesarSystemSchedulerConfig.getReleaseStatus() == schedulerSystemSchedulerConfig.getReleaseStatus()){ // 状态一致
                            equal = true;
                            break;
                        }
                    }
                }
                if(!equal){
                    diffScheduleConfigCache.get("schedule").add(schedulerSystemSchedulerConfig);
                }
            }

            // * 执行同步动作
            try {
                if (diffScheduleConfigCache.get("caesar").size() > 0) { // 这部分数据需要同步到Schedule系统
                    List<CaesarScheduleConfigInfoBo> caesarDiffSchedules = diffScheduleConfigCache.get("caesar");
                    logger.info(String.format("SYNC DIFF 1: 发现Caesar系统调度未及时同步外部调度系统情况，本次涉及差异调度信息为: %s",JSONUtils.getJSONArrayFromList(caesarDiffSchedules)));

                    for (CaesarScheduleConfigInfoBo caesarDiffSchedule : caesarDiffSchedules) {
                        CaesarTask taskInfo = taskMapper.getTaskInfoFromId(caesarDiffSchedule.getTaskId());
                        SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfigFromScheduleInfoBo(caesarDiffSchedule, allCaesarSystemSchedulerConfigByPeriod, taskInfo);
                        ScheduleResponse scheduleResponse = schedulerFacade.deployTask(schedulerModel); // 执行同步
                        if(scheduleResponse.getCode() == 200){
                        } else {
                            logger.info(String.format("Caesar向外部调度系统同步调度[%s]失败.",caesarDiffSchedule.toString()));
                        }
                    }
                }

                // 暂不实现
                if (diffScheduleConfigCache.get("schedule").size() > 0) { // 这部分需要从Schedule同步到Caesar
                    List<CaesarScheduleConfigInfoBo> scheduleDiffCaesars = diffScheduleConfigCache.get("schedule");
                    logger.info(String.format("SYNC DIFF 2: 发现调度系统项目%s调度不在Caesar注册情况发生，本次涉及差异调度信息为: %s",project, JSONUtils.getJSONArrayFromList(scheduleDiffCaesars)));

                    for(CaesarScheduleConfigInfoBo scheduleDiffCaesar: scheduleDiffCaesars){
                    }
                }
            }catch (Exception e){ // 执行失败，留给手工处理
                e.printStackTrace();
            }

//        allSchedulerConfig.addAll(diffScheduleConfigCache.get("schedule")); // 暂不考虑
        }

        for(CaesarScheduleConfigInfoBo allCaesarSystemSchedulerConfig :allCaesarSystemSchedulerConfigs){
            if(allCaesarSystemSchedulerConfig.getReleaseStatus() == 1) { // 只返回在线调度
                allSchedulerConfig.add(allCaesarSystemSchedulerConfig);
            }
        }

        if(OsUtils.isTest()){
            logger.info(String.format("Caesar数据开发平台调度任务列表: \n\t=> %s" ,allSchedulerConfig.toString()));
        }

        return allSchedulerConfig;
    }

    @Override
    public CaesarScheduleConfigDto findScheduleConfigFromScheduleName(String scheduleName) {
        return scheduleConfigMapper.findScheduleConfigFromScheduleName(scheduleName);
    }

    @Override
    public Boolean validateTaskDeploySchedule(String taskName) {
        return scheduleConfigMapper.validateTaskDeploySchedule(taskName);
    }



    @Override
    public void releaseTask(int taskId) {

        CaesarTask caesarTask = taskMapper.getTaskInfoFromId(taskId);
        List<CaesarScheduleConfigInfoBo> allCaesarSystemSchedulerConfigs = scheduleConfigMapper.getAllCaesarSystemSchedulerConfigs();
        for(CaesarScheduleConfigInfoBo scheduleConfigInfo: allCaesarSystemSchedulerConfigs){
            if(caesarTask.getTaskName().equals(scheduleConfigInfo.getTaskName()) && scheduleConfigInfo.getReleaseStatus() == 2){
                List<CaesarScheduleConfigInfoBo> caesarScheduleConfigInfoBosByPeriod = new ArrayList<>();
                for(CaesarScheduleConfigInfoBo tmpConfig: allCaesarSystemSchedulerConfigs){
                    if(scheduleConfigInfo.getPeriod().equals(tmpConfig.getPeriod())){
                        caesarScheduleConfigInfoBosByPeriod.add(tmpConfig);
                    }
                }

                try {
                    SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfigFromScheduleInfoBo(scheduleConfigInfo, caesarScheduleConfigInfoBosByPeriod, caesarTask);
                    schedulerModel.setReleaseState(1); // 上线
                    ScheduleResponse scheduleResponse = schedulerFacade.deployTask(schedulerModel); // 执行同步
                    if(scheduleResponse.getCode() == 200){
                    } else {
                        logger.info(String.format("Caesar向外部调度系统同步调度[%s]失败.",scheduleConfigInfo.toString()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }



    @Override
    public synchronized Boolean genTaskSchedule(GeneralScheduleInfoVo scheduleInfo) {

        logger.info(String.format("正在为任务%s创建调度,调度配置信息: %s",scheduleInfo.getTaskName(),scheduleInfo.toString()));

        CaesarScheduleConfig scheduleConfig = BeanConverterTools.convert(scheduleInfo, CaesarScheduleConfig.class);
        scheduleConfig.setScheduleCode(UUID.randomUUID().toString().replaceAll("-",""));
        scheduleConfig.setVersion(ScheduleVersionUtils.getInstance(scheduleConfigMapper).getVersion());
        scheduleConfig.setOwnerId(userMapper.getUserIdFromUserName(scheduleInfo.getOwnerName()));
        scheduleConfig.setTaskId(taskMapper.getTaskIdByVersion(scheduleInfo.getTaskVersion()));
        scheduleConfig.setReleaseStatus(2); // 创建任务不能在线,发版才能更改为在线状态
        scheduleConfigMapper.genTaskSchedule(scheduleConfig);


        String scheduleCode = scheduleConfig.getScheduleCode();
        for(GeneralScheduleInfoVo.Dependency dependency: Optional.ofNullable(scheduleInfo.getDependency()).orElse(new ArrayList<>())){
            dependency.setPreScheduleCode(scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(dependency.getPreScheduleName()));
            CaesarScheduleConfig preScheduleConfig = scheduleConfigMapper.getSscheduleConfigByScheduleCode(dependency.getPreScheduleCode());
            if(null != preScheduleConfig && preScheduleConfig.getReleaseStatus() == 1){
                this.updateTaskScheduleDenpendency(scheduleCode,dependency);
            } else {
                logger.info(String.format("依赖任务%s不在线,不能配置依赖.",dependency.toString()));
            }
        }


        String shellScript = CaesarScheduleUtils.getTaskProductionExecuteShellScript(scheduleInfo.getPeriod(), taskMapper.getTaskInfoFromVersion(scheduleInfo.getTaskVersion()));
        scheduleInfo.setTaskCode(shellScript);

        SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfigFromScheduleInfoVo(scheduleInfo);
        ScheduleResponse scheduleResponse = schedulerFacade.deployTask(schedulerModel);

        if(scheduleResponse.getCode() == 200){
            return true;
        }else{
            logger.info(String.format("调度%s部署失败",schedulerModel.toString()));
        }

        return false;

    }


    @Override
    public synchronized Boolean updateTaskSchedule(GeneralScheduleInfoVo scheduleInfo) {

        logger.info(String.format("开始执行调度更新，任务: %s",scheduleInfo.toString()));


        String scheduleCode = scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(scheduleInfo.getScheduleName()); // schedule_code 和 schedule_name 均唯一
        CaesarScheduleConfig scheduleConfig = BeanConverterTools.convert(scheduleInfo, CaesarScheduleConfig.class);
        scheduleConfig.setScheduleCode(scheduleCode);
        scheduleConfig.setVersion(ScheduleVersionUtils.getInstance(scheduleConfigMapper).getVersion());
        scheduleConfig.setOwnerId(userMapper.getUserIdFromUserName(scheduleInfo.getOwnerName()));
        scheduleConfig.setTaskId(taskMapper.getTaskIdByVersion(scheduleInfo.getTaskVersion()));
        scheduleConfigMapper.updateTaskSchedule(scheduleConfig);

        for(GeneralScheduleInfoVo.Dependency dependency :Optional.ofNullable(scheduleInfo.getDependency()).orElse(new ArrayList<>())){
//            dependency.setPreScheduleName(scheduleConfigMapper.getScheduleNameFromScheduleCode(dependency.getPreScheduleCode()));
            dependency.setPreScheduleCode(scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(dependency.getPreScheduleName()));
            CaesarScheduleConfig preScheduleConfig = scheduleConfigMapper.getSscheduleConfigByScheduleCode(dependency.getPreScheduleCode());
            if(null != preScheduleConfig && preScheduleConfig.getReleaseStatus() == 1){
                this.updateTaskScheduleDenpendency(scheduleCode,dependency);
            } else {
                logger.info(String.format("依赖任务%s不在线,不能配置依赖.",dependency.toString()));
            }
        }

        String shellScript = CaesarScheduleUtils.getTaskProductionExecuteShellScript(scheduleInfo.getPeriod(), taskMapper.getTaskInfoFromVersion(scheduleInfo.getTaskVersion()));
        scheduleInfo.setTaskCode(shellScript);

        SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfigFromScheduleInfoVo(scheduleInfo);
        ScheduleResponse scheduleResponse = schedulerFacade.deployTask(schedulerModel);

        if(scheduleResponse.getCode() == 200){
            return true;
        }else{
            logger.info(String.format("调度%s部署失败",schedulerModel.toString()));
        }

        return false;

    }


    @Override
    public synchronized Boolean deleteTaskSchedule(String scheduleName) {

        String scheduleCode = scheduleConfigMapper.getTaskScheduleCodeFromScheduleName(scheduleName);
        scheduleDependencyMapper.deleteByScheduleCode(scheduleCode);
        scheduleConfigMapper.deleteByScheduleCode(scheduleCode);

        SchedulerModel scheduleModel = CaesarScheduleUtils.getDeleteScheduleModel(scheduleName);
        ScheduleResponse scheduleResponse = schedulerFacade.deleteTask(scheduleModel);

        if(scheduleResponse.getCode() == 200){
            return true;
        }else{
            logger.info(String.format("调度%s部署失败",scheduleModel.toString()));
        }

        return false;
    }


    @Override
    public Boolean updateTaskScheduleDenpendency(String scheduleCode, GeneralScheduleInfoVo.Dependency dependency) {
        CaesarScheduleDependency caesarScheduleDependency = scheduleDependencyMapper.findTaskDependency(scheduleCode,dependency.getPreScheduleCode());

        Boolean flag = false;
        if(null == caesarScheduleDependency) {
            CaesarScheduleDependency scheduleDependency = BeanConverterTools.convert(dependency, CaesarScheduleDependency.class);
            scheduleDependency.setScheduleCode(scheduleCode);
            scheduleDependency.setOwnerId(userMapper.getUserIdFromUserName(dependency.getOwnerName()));
            flag = scheduleDependencyMapper.saveTaskDependency(scheduleDependency);
        }else {
            caesarScheduleDependency.setJoinType(dependency.getJoinType());
            caesarScheduleDependency.setOwnerId(userMapper.getUserIdFromUserName(dependency.getOwnerName()));
            flag = scheduleDependencyMapper.updateTaskDependency(caesarScheduleDependency);
        }

        return flag;
    }

}
