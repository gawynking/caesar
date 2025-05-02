package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserGroupVo;
import com.caesar.mapper.UserGroupMapper;
import com.caesar.service.UserGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, CaesarUserGroup> implements UserGroupService {

    @Resource
    UserGroupMapper userGroupMapper;

    @Override
    public boolean deleteUser(int userId) {
        return userGroupMapper.deleteUser(userId);
    }

    @Override
    public List<CaesarUserGroupVo> getAllGroups() {
        return userGroupMapper.getAllGroups();
    }

    @Override
    public List<CaesarUserGroup> getUserGroupsByGroupId(int groupId) {
        return userGroupMapper.getUserGroupsByGroupId(groupId);
    }
}
