package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseEntity {
    int id;
    Timestamp createTime;
    Timestamp updateTime;
}
