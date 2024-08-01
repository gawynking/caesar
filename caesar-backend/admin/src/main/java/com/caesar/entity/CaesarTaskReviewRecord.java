package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarTaskReviewRecord extends BaseEntity{

    String uuid;
    int TaskId;
    int reviewLevel;
    int reviewUser;
    int reviewStatus;
    int reviewResult;

}
