package com.caesar.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class CaesarUserVo {
    String username;
    String password;
    String email;
    String phone;
    @TableField("is_activated")
    int isActivated;

    int teamGroup;

}
