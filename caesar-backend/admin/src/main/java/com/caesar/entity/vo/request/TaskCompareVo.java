package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class TaskCompareVo {

    int taskId;
    String taskCode;
    int compareTaskId;
    String compareTaskCode;

}
