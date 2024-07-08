package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarTaskTemplate extends BaseEntity{

    String tempName;
    int taskType;
    int ownerId;
    int isDefault;
    String taskScript;

}
