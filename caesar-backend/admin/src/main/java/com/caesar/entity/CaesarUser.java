package com.caesar.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarUser {
    int id;
    String username;
    String password;
    String email;
    String phone;
    String teamGroup;
    int roleId;
    @TableField("is_effective")
    int isEffective;
    Timestamp createTime;
    Timestamp updateTime;
}
