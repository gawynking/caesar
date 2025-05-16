package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarUser;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserVo;

import java.util.List;

public interface UserManagerService extends IService<CaesarUser> {

    Integer getUserIdFromUserName(String userName);

    boolean save(CaesarUser user);

    CaesarUser findByUsername(String username);

    Boolean activatedUser(int id);

    boolean delete(int id);

    Boolean addUser(CaesarUser user);

    boolean addUserGroup(CaesarUserGroup userGroup);

    List<CaesarUserVo> getUserList();

    Boolean validAdminUser(String userName);
}
