package com.caesar.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaesarTaskExecuteRecord extends BaseEntity{

    int taskId;
    String taskName;
    String environment;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    int isSuccess;
    String taskLog;

}
