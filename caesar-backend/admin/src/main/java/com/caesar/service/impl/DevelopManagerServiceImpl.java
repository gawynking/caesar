package com.caesar.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.vo.CaesarTaskParameterVo;
import com.caesar.entity.vo.response.CaesarTaskVersionVo;
import com.caesar.enums.EngineEnum;
import com.caesar.mapper.*;
import com.caesar.model.MenuModel;
import com.caesar.model.code.TemplateAssembler;
import com.caesar.model.code.model.dto.TaskTemplateDto;
import com.caesar.service.DevelopCenterService;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.service.MenuManagerService;
import com.caesar.service.TeamGroupService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class DevelopManagerServiceImpl extends ServiceImpl<TaskMapper, CaesarTask> implements DevelopCenterService {


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
    public List<MenuModel> listTaskToMenu(String partten) {
        if(null == partten || "".equals(partten.trim())){
            return taskMapper.listTaskToMenu();
        }
        String[] parttens = partten.split("\\.");
        if(parttens.length==1){
            String parttenStr = parttens[0].equals("*")?"%%":"%"+parttens[0]+"%";
            return taskMapper.listTaskToMenuByOwnerAndTaskname(parttenStr,"%%");
        } else{
            String parttenStr1 = parttens[0].equals("*")?"%%":"%"+parttens[0]+"%";
            String parttenStr2 = parttens[1].equals("*")?"%%":"%"+parttens[1]+"%";
            return taskMapper.listTaskToMenuByOwnerAndTaskname(parttenStr1,parttenStr2);
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
            if(null == taskScript) taskScript = defaultTaskScript;
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


}
