package com.caesar.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;


@Data
public class CaesarTask extends BaseEntity{

    int menuId;
    int taskType;
    String taskName;
    String datasourceInfo;
    int execEngine;
    int version;
    int groupId;
    int isReleased;
    int isOnline;
    int isDelete=0;
    String taskScript;
    int createdUser;
    int updatedUser;

}
