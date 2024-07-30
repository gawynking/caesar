package com.caesar.entity.dto;

import com.caesar.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaesarTaskExecuteRecordDto {

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
