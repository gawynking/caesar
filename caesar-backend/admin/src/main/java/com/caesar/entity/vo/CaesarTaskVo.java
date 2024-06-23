package com.caesar.entity.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarTaskVo {

    int menuId;
    int taskType;
    String taskName;
    String datasourceInfo;
    int execEngine;
    int version;
    int createdUser;
    int updatedUser;
    int groupId;
    int isReleased;
    int isOnline;
    String taskScript;

}
