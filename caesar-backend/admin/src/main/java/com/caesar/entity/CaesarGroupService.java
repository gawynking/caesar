package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarGroupService extends BaseEntity{

    int groupId;
    String serviceTag;
    String levelTag;
    int isTest;

}
