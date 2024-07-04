package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.mapper.DatasourceMapper;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.TaskTemplateMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.model.MenuModel;
import com.caesar.service.TaskService;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.DatasourceUtils;
import com.caesar.util.TaskVersionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, CaesarTask> implements TaskService {

    @Resource
    TaskMapper taskMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    TaskTemplateMapper taskTemplateMapper;

    @Resource
    DatasourceMapper datasourceMapper;




    @Override
    public List<MenuModel> listTask(String partten) {
        if(null == partten || "".equals(partten.trim())){
            return taskMapper.listToMenu();
        }
        String[] parttens = partten.split("\\.");
        if(parttens.length==1){
            String parttenStr = parttens[0].equals("*")?"%%":"%"+parttens[0]+"%";
            return taskMapper.listLikeByOwnerAndTaskNameToMenu(parttenStr,"%%");
        } else{
            String parttenStr1 = parttens[0].equals("*")?"%%":"%"+parttens[0]+"%";
            String parttenStr2 = parttens[1].equals("*")?"%%":"%"+parttens[1]+"%";
            return taskMapper.listLikeByOwnerAndTaskNameToMenu(parttenStr1,parttenStr2);
        }
    }

    @Override
    public boolean addTask(CaesarTaskDto taskDto) {
        List<CaesarTask> tasks = taskMapper.findByName(taskDto.getTaskName());
        if(null == tasks || tasks.size() == 0){
            int version = TaskVersionUtils.getInstance(taskMapper).getVersion();
            int teamGroup = userMapper.getTeamGroup(taskDto.getCreatedUser());
            taskDto.setVersion(version);
            taskDto.setGroupId(teamGroup);
            taskDto.setIsReleased(0);
            taskDto.setIsOnline(0);
            // datasource ,taskScript
            String taskScript = taskTemplateMapper.getTaskTemplateScript(taskDto.getCreatedUser(),taskDto.getTaskType());
            if(null == taskScript) taskScript="";
            taskDto.setTaskScript(taskScript);
            String datasourceInfo = DatasourceUtils.getDatasourceInfo(datasourceMapper.getDatasourceInfo(taskDto.getEngine()));
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
            return taskMapper.martDeleteToOnline(taskDto.getTaskName());
        }

        return false;
    }

    @Override
    public CaesarTaskVo getCurrentTaskInfo(String taskName) {
        return taskMapper.getCurrentTaskInfo(taskName);
    }

    @Override
    public List<CaesarTaskVo> getTaskInfo(String taskName) {
        return taskMapper.getTaskInfo(taskName);
    }

    @Override
    public Boolean deleteTaskFromTaskName(String taskName) {
        return taskMapper.deleteTaskFromTaskName(taskName);
    }

    @Override
    public Boolean markDeleteTaskFromTaskName(String taskName) {
        return taskMapper.markDeleteTaskFromTaskName(taskName);
    }


}
