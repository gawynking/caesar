package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarMenu;
import com.caesar.entity.dto.CaesarMenuDto;
import com.caesar.model.MenuModel;

import java.util.List;


public interface MenuManagerService extends IService<CaesarMenu> {

    boolean addFolder(CaesarMenuDto menu);

    boolean deleteFolder(Integer id);

    boolean renameFolder(CaesarMenuDto caesarMenuDto);

    List<MenuModel> listMenuForAside();

    boolean existsSubtask(int id);

    boolean isTaskFolder(int parentId);

    Integer getMenuIdFromMenuIndex(String menuIndex);

}
