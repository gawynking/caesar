package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarUser;
import com.caesar.mapper.UserMapper;
import com.caesar.service.LoginService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper, CaesarUser> implements LoginService {

    @Resource
    UserMapper userMapper;

    @Override
    public CaesarUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

}
