package com.caesar.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaesarTaskExecuteRecordDto {

    String planUuid;
    String uuid;
    int taskId;
    String taskName;
    String environment;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    int isSuccess;
    String taskLog;
    // JSON结构
    String parameter;

}
