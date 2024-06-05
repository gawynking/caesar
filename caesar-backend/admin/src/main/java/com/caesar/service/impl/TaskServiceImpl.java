package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTask;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.model.MenuModel;
import com.caesar.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, CaesarTask> implements TaskService {

    @Resource
    TaskMapper taskMapper;

    @Resource
    UserMapper userMapper;

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
    public boolean addTask(CaesarTask task) {
        List<CaesarTask> tasks = taskMapper.findByName(task.getTaskName());
        if(null == tasks){
            int version = 1;
            for(CaesarTask item:tasks){
                int tmpVersion = item.getVersion();
                if(tmpVersion>version){
                    version=tmpVersion;
                }
            }
            int taskType = 1;
            int groupId = userMapper.getGoupId(task.getUpdatedUser());
            task.setVersion(version);
            task.setTaskType(taskType);
            task.setGroupId(groupId);
            return taskMapper.addTask(task);
        }
        return false;
    }


}
