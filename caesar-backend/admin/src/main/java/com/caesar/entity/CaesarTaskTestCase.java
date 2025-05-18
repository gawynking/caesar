package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarTaskTestCase extends BaseEntity{

    String uuid;
    int TaskId;
    String taskName;
    int taskVersion;
    int userId;
    String testCode;
    int testResult = 0;
    String auditMessage = "";

}
