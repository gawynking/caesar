package com.caesar.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class CaesarUserVo {

    int id;
    String username;
    String password;
    String email;
    String phone;
    @TableField("is_activated")
    int isActivated;

    int teamGroup;

    List<CaesarUserGroupVo> groups;

}
