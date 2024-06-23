package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class CaesarTeamGroup extends BaseEntity{

    String groupName;
    String groupDesc;
    int ownerId;

}
