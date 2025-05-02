package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class TaskExecuteVo {

    String taskName;
    int version;
    String environment;

}
