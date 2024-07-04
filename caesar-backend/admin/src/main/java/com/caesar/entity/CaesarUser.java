package com.caesar.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarUser extends BaseEntity{

    String username;
    String password;
    String email;
    String phone;
    String teamGroup;
    @TableField("is_effective")
    int isEffective;

}
