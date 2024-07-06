package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.model.MenuModel;
import com.caesar.entity.vo.CaesarTaskVo;

import java.util.List;


public interface DevelopCenterService extends IService<CaesarTask> {

    List<MenuModel> listTaskToMenu(String partten);

    boolean addTask(CaesarTaskDto task);

    CaesarTaskVo getCurrentTaskInfo(String taskName);

    List<CaesarTaskVo> getTaskInfos(String taskName);

    Boolean deleteTaskFromTaskName(String taskName);

    Boolean markDeletedTaskFromTaskName(String taskName);

}
