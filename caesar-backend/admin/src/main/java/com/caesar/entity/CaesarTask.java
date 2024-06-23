package com.caesar.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;


@Data
@Builder
public class CaesarTask {

    int id;
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
    Timestamp createTime;
    Timestamp updateTime;

}
