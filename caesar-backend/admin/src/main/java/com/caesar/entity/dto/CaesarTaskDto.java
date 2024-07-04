package com.caesar.entity.dto;

import lombok.Data;

@Data
public class CaesarTaskDto {
    int menuId;
    int taskType;
    String taskName;
    String datasourceInfo;
    int engine;
    int version;
    int groupId;
    int isReleased;
    int isOnline;
    int isDeleted;
    String taskScript;
    String checksum;
    int createdUser;
    int updatedUser;
}
