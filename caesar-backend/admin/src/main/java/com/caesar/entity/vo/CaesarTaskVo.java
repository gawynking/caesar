package com.caesar.entity.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarTaskVo {

    int id;
    int menuId;
    int taskType;
    String taskName;
    String datasourceInfo;
    int engine;
    int version;
    int lastVersion;
    int groupId;
    int isReleased;
    int isOnline;
    int isDeleted;
    String taskScript;
    String checksum;
    int createdUser;
    int updatedUser;
    Timestamp createTime;

}
