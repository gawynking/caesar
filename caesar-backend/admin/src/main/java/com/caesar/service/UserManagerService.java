package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarUser;


public interface UserManagerService extends IService<CaesarUser> {

    Integer getUserIdFromUserName(String userName);
}
