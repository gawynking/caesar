package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarTaskReviewRecord extends BaseEntity{

    String uuid;
    int TaskId;
    String taskName;
    int taskVersion;
    int preVersion;
    int submitUserId;
    String codeDesc;
    int reviewLevel;
    String reviewUsers;
    int reviewUser;
    int reviewStatus;
    int reviewResult;
    String auditMessage;

}
