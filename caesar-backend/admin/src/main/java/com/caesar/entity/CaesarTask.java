package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class CaesarTask {

    int id;
    int menuId;
    int taskType;
    String taskName;
    int execEngine;
    int version;
    int createdUser;
    int updatedUser;
    int groupId;
    String taskScript;
    Timestamp createTime;
    Timestamp updateTime;

}
