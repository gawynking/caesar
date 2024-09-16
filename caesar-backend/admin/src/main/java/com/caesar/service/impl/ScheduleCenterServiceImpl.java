package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.config.CaesarConfig;
import com.caesar.entity.CaesarScheduleConfig;
import com.caesar.entity.CaesarScheduleDependency;
import com.caesar.entity.dto.CaesarScheduleConfigDto;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.entity.vo.response.ScheduleBaseInfoVo;
import com.caesar.entity.vo.response.ScheduleInfoVo;
import com.caesar.mapper.ScheduleConfigMapper;
import com.caesar.mapper.ScheduleDependencyMapper;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.model.ScheduleResponse;
import com.caesar.model.SchedulerModel;
import com.caesar.model.code.TaskContentParser;
import com.caesar.model.code.TemplateUtils;
import com.caesar.scheduler.SchedulerFacade;
import com.caesar.service.ScheduleCenterService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.CaesarScheduleUtils;
import com.caesar.util.ScheduleVersionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class ScheduleCenterServiceImpl extends ServiceImpl<ScheduleConfigMapper, CaesarScheduleConfig> implements ScheduleCenterService {

    private static final Logger LOGGER = Logger.getLogger(ScheduleCenterServiceImpl.class.getName());

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
        List<CaesarScheduleDependency> taskDependencyList = scheduleDependencyMapper.getTaskScheduleDependency(scheduleName);

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

        for(CaesarScheduleDependency dependency:taskDependencyList){
            ScheduleInfoVo.Dependency dependencyItem = new ScheduleInfoVo.Dependency();
            dependencyItem.setPreScheduleCode(dependency.getPreScheduleCode());
            dependencyItem.setJoinType(dependency.getJoinType());
            dependencyItem.setOwnerId(dependency.getOwnerId());
            dependencyItem.setOwnerName(userMapper.getUsernameFromId(dependency.getOwnerId()));
            dependencys.add(dependencyItem);
        }

        return scheduleInfoVo;
    }


    @Override
    public List<ScheduleInfoVo> getTaskSchedules(String taskName) {
        List<CaesarScheduleConfigDto> taskList = scheduleConfigMapper.getTaskSchedules(taskName);
        List<CaesarScheduleDependency> taskDependencyList = scheduleDependencyMapper.getTaskScheduleDependencys(taskName);
        List<ScheduleInfoVo> scheduleInfoVos = new ArrayList<>();
        for(CaesarScheduleConfigDto scheduleConfig: taskList){
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
            for(CaesarScheduleDependency dependency:taskDependencyList){
                if(scheduleConfig.getScheduleCode().equals(dependency.getScheduleCode())){
                    ScheduleInfoVo.Dependency dependencyItem = new ScheduleInfoVo.Dependency();
                    dependencyItem.setPreScheduleCode(dependency.getPreScheduleCode());
                    dependencyItem.setJoinType(dependency.getJoinType());
                    dependencyItem.setOwnerId(dependency.getOwnerId());
                    dependencyItem.setOwnerName(userMapper.getUsernameFromId(dependency.getOwnerId()));
                    dependencys.add(dependencyItem);
                }
            }
        }
        return scheduleInfoVos;
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
            dependency.setPreScheduleName(scheduleConfigMapper.getScheduleNameFromScheduleCode(dependency.getPreScheduleCode()));
            Boolean insertTaskDependencyFlag = updateTaskScheduleDenpendency(scheduleCode,dependency);
            if(!insertTaskDependencyFlag){
                flag = false;
            }
        }

        String rawTaskCode = taskMapper.getCurrentTaskInfoWithVersion(scheduleInfo.getTaskName(),scheduleInfo.getTaskVersion()).getTaskScript();
        TaskContentParser taskContentParser = new TaskContentParser(rawTaskCode);
        String taskCode = TemplateUtils.transformSqlTemplate(taskContentParser);
        scheduleInfo.setTaskCode(taskCode);

        LOGGER.info("本次创建任务执行脚本如下: " + taskCode);

        SchedulerFacade schedulerClient = CaesarScheduleUtils.getSchedulerClient();
        SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfig(scheduleInfo);
        ScheduleResponse scheduleResponse = schedulerClient.deployTask(schedulerModel);

        LOGGER.log(Level.INFO,"任务(0)创建(1),结果(2).",new String[]{scheduleInfo.getScheduleName(),scheduleResponse.getCode().toString(),scheduleResponse.getData().toString()});

        return flag;

    }


    @Override
    public Boolean updateTaskSchedule(GeneralScheduleInfoVo scheduleInfo) {
        Boolean flag = false;
        String scheduleCode = scheduleInfo.getScheduleCode();
        CaesarScheduleConfig scheduleConfig = BeanConverterTools.convert(scheduleInfo, CaesarScheduleConfig.class);
        scheduleConfig.setScheduleCode(scheduleCode);
        scheduleConfig.setVersion(ScheduleVersionUtils.getInstance(scheduleConfigMapper).getVersion());
        scheduleConfig.setOwnerId(userMapper.getUserIdFromUserName(scheduleInfo.getOwnerName()));
        Boolean updateTaskScheduleFlag = scheduleConfigMapper.updateTaskSchedule(scheduleConfig);

        flag = updateTaskScheduleFlag;
        for(GeneralScheduleInfoVo.Dependency dependency:scheduleInfo.getDependency()){
            dependency.setPreScheduleName(scheduleConfigMapper.getScheduleNameFromScheduleCode(dependency.getPreScheduleCode()));
            Boolean insertTaskDependencyFlag = updateTaskScheduleDenpendency(scheduleCode,dependency);
            if(!insertTaskDependencyFlag){
                flag = false;
            }
        }

        String rawTaskCode = taskMapper.getCurrentTaskInfoWithVersion(scheduleInfo.getTaskName(),scheduleInfo.getTaskVersion()).getTaskScript();
        TaskContentParser taskContentParser = new TaskContentParser(rawTaskCode);
        String taskCode = TemplateUtils.transformSqlTemplate(taskContentParser);
        scheduleInfo.setTaskCode(taskCode);

        LOGGER.info("本次创更新务执行脚本如下: " + taskCode);

        SchedulerFacade schedulerClient = CaesarScheduleUtils.getSchedulerClient();
        SchedulerModel schedulerModel = CaesarScheduleUtils.convertScheduleConfig(scheduleInfo);
        ScheduleResponse scheduleResponse = schedulerClient.deployTask(schedulerModel);

        LOGGER.log(Level.INFO,"任务(0)更新(1),结果(2).",new String[]{scheduleInfo.getScheduleName(),scheduleResponse.getCode().toString(),scheduleResponse.getData().toString()});

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
        LOGGER.info(String.format("删除调度任务(%s),Code (%s);结果 (%s)",scheduleName,scheduleResponse.getCode(),scheduleResponse.getData()));

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
