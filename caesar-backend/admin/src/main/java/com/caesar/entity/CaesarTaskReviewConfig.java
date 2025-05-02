package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarTaskReviewConfig extends BaseEntity{

    int taskId;
    String uuid;
    int reviewLevel;
    int reviewUserId;
    String reviewUsername;
    String reviewDesc;

}
