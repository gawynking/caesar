package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarScheduleDependency extends BaseEntity{

    String scheduleCode;
    String preScheduleCode;
    int joinType = 1;
    int ownerId;

}
