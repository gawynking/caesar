package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarTeamGroup {
    int id;
    String groupName;
    String groupDesc;
    int ownerId;
    Timestamp createTime;
    Timestamp updateTime;
}
