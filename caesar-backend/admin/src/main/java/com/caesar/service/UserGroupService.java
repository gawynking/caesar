package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserGroupVo;

import java.util.List;


public interface UserGroupService extends IService<CaesarUserGroup> {

    boolean deleteUser(int id);

    List<CaesarUserGroupVo> getAllGroups();

    List<CaesarUserGroup> getUserGroupsByGroupId(int groupId);
}