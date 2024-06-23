package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarTaskTemplate extends BaseEntity{

    int taskType;
    int ownerId;
    String taskScript;

}
