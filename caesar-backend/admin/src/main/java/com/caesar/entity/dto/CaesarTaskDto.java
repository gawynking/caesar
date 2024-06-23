package com.caesar.entity.dto;

import lombok.Data;

@Data
public class CaesarTaskDto {

    int menuId;
    int taskType;
    String taskName;
    String datasourceInfo;
    int execEngine;
    int version;
    int groupId;
    int isReleased;
    int isOnline;
    int isDelete;
    String taskScript;
    int createdUser;
    int updatedUser;

}
