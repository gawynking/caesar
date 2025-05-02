package com.caesar.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class CaesarUser extends BaseEntity{

    String username;
    String password;
    String email;
    String phone;
    @TableField("is_activated")
    int isActivated;

}
