package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarTaskTemplate extends BaseEntity{

    String tempName;
    int taskType;
    int ownerId;
    int isDefault;
    String taskScript;

}
