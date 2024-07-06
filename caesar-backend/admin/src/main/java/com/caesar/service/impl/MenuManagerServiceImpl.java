package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarMenu;
import com.caesar.entity.dto.CaesarMenuDto;
import com.caesar.mapper.MenuMapper;
import com.caesar.model.MenuModel;
import com.caesar.service.MenuManagerService;
import com.caesar.tool.BeanConverterTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MenuManagerServiceImpl extends ServiceImpl<MenuMapper, CaesarMenu> implements MenuManagerService {

    @Resource
    MenuMapper menuMapper;


    @Override
    public boolean addFolder(CaesarMenuDto menu) {
        CaesarMenu caesarMenu = BeanConverterTools.convert(menu, CaesarMenu.class);
        return menuMapper.addFolder(caesarMenu);
    }

    @Override
    public boolean deleteFolder(Integer id) {
        CaesarMenu menu = menuMapper.findById(id);
        if (null == menu){
            return true;
        } else if(!existsSubtask(id)){
            return menuMapper.deleteFolder(id);
        }
        return false;
    }

    @Override
    public boolean renameFolder(CaesarMenuDto menu) {
        CaesarMenu caesarMenu = BeanConverterTools.convert(menu, CaesarMenu.class);
        return menuMapper.renameFolder(caesarMenu);
    }

    @Override
    public List<MenuModel> listMenuForAside() {
        return menuMapper.listMenuForAside();
    }

    @Override
    public boolean existsSubtask(int id){
        Integer subtaskNum = menuMapper.findSubtaskNumber(id);
        if(null == subtaskNum || subtaskNum == 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean isTaskFolder(int parentId){
        List<Integer> parentIds = menuMapper.findAllowedSubtaskNodes();
        if(parentIds.contains(parentId)){
            return true;
        }
        return false;
    }

    @Override
    public Integer getMenuIdFromMenuIndex(String menuIndex) {
        Integer menuId = menuMapper.getMenuIdFromMenuIndex(menuIndex);
        return menuId;
    }

}
