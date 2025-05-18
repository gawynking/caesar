package com.caesar.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaesarTaskExecuteRecord extends BaseEntity{

    String planUuid;
    String uuid;
    int taskId;
    String taskName;
    String parameter;
    String environment;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    int isSuccess;
    String taskLog;

}

