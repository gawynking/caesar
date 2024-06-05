package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTask;
import com.caesar.model.MenuModel;

import java.util.List;


public interface TaskService extends IService<CaesarTask> {

    List<MenuModel> listTask(String partten);

    boolean addTask(CaesarTask task);
}
