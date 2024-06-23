package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.model.MenuModel;
import com.caesar.entity.vo.CaesarTaskVo;

import java.util.List;


public interface TaskService extends IService<CaesarTask> {

    List<MenuModel> listTask(String partten);

    boolean addTask(CaesarTaskDto task);

    CaesarTaskVo getCurrentTaskInfo(String taskName);

    List<CaesarTaskVo> getTaskInfo(String taskName);
}
