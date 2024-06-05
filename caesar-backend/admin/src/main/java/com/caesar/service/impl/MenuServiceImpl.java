package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarMenu;
import com.caesar.mapper.MenuMapper;
import com.caesar.model.MenuModel;
import com.caesar.service.MenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, CaesarMenu> implements MenuService {

    @Resource
    MenuMapper menuMapper;

    @Override
    public boolean addFolder(CaesarMenu menu) {
        return menuMapper.addFolder(menu);
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
    public List<MenuModel> listByAside() {
        return menuMapper.listByAside();
    }

    @Override
    public boolean existsSubtask(int id){
        Integer subtaskNum = menuMapper.findSubtask(id);
        if(null == subtaskNum || subtaskNum == 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean isTaskFolder(int parentId){
        List<Integer> parentIds = menuMapper.allowedSubtask();
        if(parentIds.contains(parentId)){
            return true;
        }
        return false;
    }

}