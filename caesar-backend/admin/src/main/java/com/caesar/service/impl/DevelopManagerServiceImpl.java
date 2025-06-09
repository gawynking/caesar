package com.caesar.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.config.TemplateMapping;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.vo.CaesarTaskParameterVo;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.entity.vo.response.CaesarTaskVersionVo;
import com.caesar.enums.EngineEnum;
import com.caesar.mapper.*;
import com.caesar.model.MenuModel;
import com.caesar.model.code.TemplateAssembler;
import com.caesar.model.code.model.dto.TaskTemplateDto;
import com.caesar.service.DevelopCenterService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.*;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;


@Service
public class DevelopManagerServiceImpl extends ServiceImpl<TaskMapper, CaesarTask> implements DevelopCenterService {

    private static final Logger logger = Logger.getLogger(DevelopManagerServiceImpl.class.getName());

    @Resource
    TaskMapper taskMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    TaskTemplateMapper taskTemplateMapper;

    @Resource
    DatasourceMapper datasourceMapper;

    @Resource
    MenuMapper menuMapper;

    @Resource
    TeamGroupMapper teamGroupMapper;


    @Override
    public String getCodeTemplate(EngineEnum engine){
        if(null == engine){
            return defaultTaskScript;
        }
        String path = TemplateMapping.getTemplatePath(engine);
        org.springframework.core.io.Resource resource = new ClassPathResource(path);
        try {
            return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultTaskScript;
    }

    @Override
    public List<MenuModel> listTaskToMenu(String pattern) {
        pattern = pattern.replaceAll("\\s","");
        if(null == pattern || "".equals(pattern) || ".".equals(pattern) || "*".equals(pattern)){
            return taskMapper.listTaskToMenu();
        }
        String[] patterns = pattern.split("\\.");
        if(patterns.length==1){
            String patternStr = "%"+pattern.replaceAll("\\.","")+"%";
            return taskMapper.listTaskToMenuByOwnerOrTaskname(patternStr);
        } else{
            if(null == patterns[0] || "".equals(patterns[0])){
                String patternStr1 = "%%";
                String patternStr2 = patterns[1].equals("*") ? "%%" : "%" + patterns[1] + "%";
                return taskMapper.listTaskToMenuByOwnerAndTaskname(patternStr1,patternStr2);
            } else {
                String patternStr1 = patterns[0].equals("*") ? "%%" : "%" + patterns[0] + "%";
                String patternStr2 = patterns[1].equals("*") ? "%%" : "%" + patterns[1] + "%";
                return taskMapper.listTaskToMenuByOwnerAndTaskname(patternStr1, patternStr2);
            }
        }
    }


    @Override
    public boolean addTask(CaesarTaskDto taskDto) {
        List<CaesarTask> tasks = taskMapper.findByName(taskDto.getTaskName());
        if(null == tasks || tasks.size() == 0){
            int version = TaskVersionUtils.getInstance(taskMapper).getVersion();
            taskDto.setVersion(version);
            taskDto.setIsReleased(0);
            taskDto.setIsOnline(0);

            JSONObject extendProperties = JSONUtils.getJSONObjectFromString(menuMapper.getExtendPropertiesByMenuId(taskDto.getMenuId()));
            if(null == extendProperties){
                return false;
            }else {
                taskDto.setTaskType(Integer.valueOf(extendProperties.getInteger("task_type")));
            }

            // datasource ,taskScript
            TaskTemplateDto taskTemplateDto = new TaskTemplateDto();
            String username = userMapper.getUsernameFromId(taskDto.getCreatedUser());
            String createTime = DateUtils.getCurrentDatetime();
            String taskName = taskDto.getTaskName();
            String[] taskNameParts = taskName.split("\\.");
            if(taskNameParts.length>2){
                taskName = taskNameParts[0]+'.'+taskNameParts[1];
            }
            String engine = EngineEnum.fromTag(taskDto.getEngine()).getEngine();
            String groupName = teamGroupMapper.getGroupNameFromId(taskDto.getGroupId());
            taskTemplateDto.setUsername(username);
            taskTemplateDto.setCreateTime(createTime);
            taskTemplateDto.setTaskName(taskName);
            taskTemplateDto.setEngine(engine);
            taskTemplateDto.setGroupName(groupName);

            String taskScript = taskTemplateMapper.getTaskTemplateScriptFromOwnerAndTasktype(taskDto.getCreatedUser(),taskDto.getTaskType());
            if(null == taskScript) taskScript = this.getCodeTemplate(EngineEnum.fromEngine(engine));
            taskScript = TemplateAssembler.taskTemplateAssembler(taskScript, taskTemplateDto);
            taskDto.setTaskScript(taskScript);
            String checksum = HashUtils.getMD5Hash(taskScript);
            taskDto.setChecksum(checksum);
            String datasourceInfo = DatasourceUtils.getDatasourceInfo(datasourceMapper.getDatasourceInfoFromEngine(taskDto.getEngine()));
            taskDto.setDatasourceInfo(datasourceInfo);

            CaesarTask caesarTask = BeanConverterTools.convert(taskDto, CaesarTask.class);

            return taskMapper.addTask(caesarTask);
        }

        boolean markDeleted = false;
        for(CaesarTask task:tasks){
            if(task.getIsDeleted() == 1){
                markDeleted=true;
            }
        }
        if(markDeleted){
            return taskMapper.recoverDeletedTaskFromTaskName(taskDto.getTaskName());
        }

        return false;
    }



    @Override
    public CaesarTaskVo getCurrentTaskInfo(String taskName) {
        return taskMapper.getCurrentTaskInfo(taskName);
    }

    @Override
    public List<CaesarTaskVo> getTaskInfos(String taskName) {
        return taskMapper.getTaskInfos(taskName);
    }

    @Override
    public Boolean deleteTaskFromTaskName(String taskName) {
        return taskMapper.deleteTaskFromTaskName(taskName);
    }

    @Override
    public Boolean markDeletedTaskFromTaskName(String taskName) {
        return taskMapper.markDeletedTaskFromTaskName(taskName);
    }

    @Override
    public String getTaskChecksumFromVersion(String taskName, int version) {
        return taskMapper.getTaskChecksumFromVersion(taskName,version);
    }

    @Override
    public int saveTask(CaesarTaskDto caesarTaskDto) {
        int version = TaskVersionUtils.getInstance(taskMapper).getVersion();
        caesarTaskDto.setVersion(version);
        caesarTaskDto.setIsDeleted(0);
        caesarTaskDto.setIsOnline(0);
        caesarTaskDto.setIsDeleted(0);

        CaesarTask caesarTask = BeanConverterTools.convert(caesarTaskDto, CaesarTask.class);
        taskMapper.addTask(caesarTask);
        return version;
    }

    @Override
    public CaesarTaskVersionVo getTaskVersions(String taskName, int currentVersion) {
        CaesarTaskVersionVo caesarTaskVersionVo = new CaesarTaskVersionVo();
        List<CaesarTaskVo> caesarTaskVos = taskMapper.getTaskVersions(taskName);
        CaesarTaskVo currentTaskInfo = taskMapper.getCurrentTaskInfo(taskName);
        if(currentVersion == -1){
            caesarTaskVersionVo.setCurrentVersion(currentTaskInfo.getVersion());
        }else{
            caesarTaskVersionVo.setCurrentVersion(currentVersion);
        }
        caesarTaskVersionVo.setCaesarTaskVos(caesarTaskVos);

        return caesarTaskVersionVo;
    }

    @Override
    public List<CaesarTaskParameterVo> getParams() {
        return taskMapper.getParams();
    }

    @Override
    public CaesarTaskVo getCurrentTaskInfoWithVersion(String taskName, int version) {
        return taskMapper.getCurrentTaskInfoWithVersion(taskName,version);
    }

    @Override
    public Boolean releaseTask(int taskId) {
        CaesarTask taskInfo = taskMapper.getTaskInfoFromId(taskId);
        List<CaesarTaskVo> taskInfos = taskMapper.getTaskInfos(taskInfo.getTaskName());
        for(CaesarTaskVo tmpTask:taskInfos){
            if(tmpTask.getVersion() < taskInfo.getVersion()){
                if(tmpTask.getIsOnline() == 1){
                    taskMapper.currentVersionTaskOffline(tmpTask.getId());
                }
            }
        }
        return taskMapper.currentVersionTaskOnline(taskId);
    }

    @Override
    public Boolean currentVersionTaskOffline(int taskId) {
        return taskMapper.currentVersionTaskOffline(taskId);
    }

    @Override
    public CaesarTask getTaskOnlineVersionInfoFromReviewTaskId(int taskId) {
        return taskMapper.getTaskOnlineVersionInfoFromReviewTaskId(taskId);
    }

    @Override
    public Boolean validateTaskPublish(int taskId) {
        return taskMapper.validateTaskPublish(taskId);
    }

    @Override
    public void taskOnline(int taskId) {

    }


}
