package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarMenuRole {

    int id;
    String roleName;
    Timestamp createTime;
    Timestamp updateTime;

}
