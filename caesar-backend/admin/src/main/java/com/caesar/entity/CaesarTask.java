package com.caesar.entity;

import lombok.Data;


@Data
public class CaesarTask extends BaseEntity{

    int menuId;
    int taskType;
    String taskName;
    String datasourceInfo;
    int engine;
    int version;
    int groupId;
    int isReleased;
    int isOnline;
    int isDeleted=0;
    String taskScript;
    String checksum;
    int createdUser;
    int updatedUser;

}
