package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarUser;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserVo;
import com.caesar.mapper.UserGroupMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.service.UserManagerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserManagerServiceImpl extends ServiceImpl<UserMapper, CaesarUser> implements UserManagerService {

    @Resource
    UserMapper userMapper;

    @Resource
    UserGroupMapper userGroupMapper;


    @Override
    public Integer getUserIdFromUserName(String userName) {
        return userMapper.getUserIdFromUserName(userName);
    }

    @Override
    public boolean save(CaesarUser user) {
        return userMapper.save(user);
    }

    @Override
    public CaesarUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Boolean activatedUser(int id) {
        return userMapper.activatedUser(id);
    }

    @Override
    public boolean delete(int id) {
        return userMapper.deleteUser(id);
    }

    @Override
    public Boolean addUser(CaesarUser user) {
        return userMapper.save(user);
    }

    @Override
    public boolean addUserGroup(CaesarUserGroup userGroup) {
        return userGroupMapper.addUserGroup(userGroup);
    }

    @Override
    public List<CaesarUserVo> getUserList() {
        return userMapper.getUserList();
    }

}
