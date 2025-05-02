package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarUser;

public interface LoginService extends IService<CaesarUser> {

    CaesarUser findByUsername(String username);

}