package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarTaskTemplate {
    int id;
    int taskType;
    int ownerId;
    int execEngine;
    String taskScript;
    Timestamp createTime;
    Timestamp updateTime;
}
