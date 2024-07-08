package com.caesar.entity;

import lombok.Data;


@Data
public class CaesarTeamGroup extends BaseEntity{

    String groupName;
    String groupDesc;
    int ownerId;

}
