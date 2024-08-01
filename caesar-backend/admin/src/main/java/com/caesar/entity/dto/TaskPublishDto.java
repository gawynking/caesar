package com.caesar.entity.dto;

import lombok.Data;


@Data
public class TaskPublishDto {

    int taskId;
    int version;
    String submitUsername;
    String codeDesc;

    int menuId;
    int taskType;
    String taskName;
    int engine;
    int groupId;
    int isReleased;
    int isOnline;
    int isDeleted;
    int createdUser;
    int updatedUser;

}
