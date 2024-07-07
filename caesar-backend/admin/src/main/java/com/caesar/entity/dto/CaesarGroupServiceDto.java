package com.caesar.entity.dto;

import lombok.Data;

@Data
public class CaesarGroupServiceDto {

    int groupId;
    String groupName;

    int id;
    String serviceTag;
    String levelTag;
    int isTest;

}
