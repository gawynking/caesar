package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarTaskReviewConfig extends BaseEntity{

    String uuid;
    int reviewLevel;
    int reviewUserId;
    String reviewUsername;
    String reviewDesc;

}
