package com.caesar.entity.dto;

import lombok.Data;

@Data
public class CaesarReviewTaskDto {

    int id;
    String reviewBatch;
    int taskId;
    String taskName;
    int version;
    String submitUsername;
    String codeDesc;
    int reviewLevel;
    int reviewStatus;
    int reviewResult;
    String createTime;

}
