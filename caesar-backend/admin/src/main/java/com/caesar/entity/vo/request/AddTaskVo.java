package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class AddTaskVo {

    String menuIndex;
    int taskType;
    String taskName;
    int engine;
    String createdUserName;
    String updatedUserName;

}
