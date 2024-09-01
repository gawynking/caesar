package com.caesar.entity.dto;

import lombok.Data;

@Data
public class CaesarReviewTaskDto {
    int id;
    String reviewBatch;
    int taskId;
    String taskName;
    int version;
    int preVersion;
    String submitUsername;
    String codeDesc;
    int reviewLevel;
    int reviewStatus;
    int reviewResult;
    String createTime;
    String currentCode;
    String lastCode;
}
