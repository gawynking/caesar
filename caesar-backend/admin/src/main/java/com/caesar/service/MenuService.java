package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarMenu;
import com.caesar.model.MenuModel;

import java.util.List;


public interface MenuService extends IService<CaesarMenu> {

    boolean addFolder(CaesarMenu menu);

    boolean deleteFolder(Integer id);

    List<MenuModel> listByAside();

    boolean existsSubtask(int id);

    boolean isTaskFolder(int parentId);
}
