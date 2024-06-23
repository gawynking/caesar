package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class AddTaskVo {

    String menuIndex;
    int taskType;
    String taskName;
    int execEngine;
    String createdUserName;
    String updatedUserName;

}
