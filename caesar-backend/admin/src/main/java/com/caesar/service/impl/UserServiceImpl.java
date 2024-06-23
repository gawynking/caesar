package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarUser;
import com.caesar.mapper.UserMapper;
import com.caesar.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, CaesarUser> implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public Integer getUserIdFromUserName(String userName) {
        Integer userId = userMapper.getUserIdFromUserName(userName);
        return userId;
    }
}
