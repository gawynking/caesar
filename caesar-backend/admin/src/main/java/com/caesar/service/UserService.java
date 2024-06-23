package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarUser;


public interface UserService extends IService<CaesarUser> {

    Integer getUserIdFromUserName(String userName);
}
