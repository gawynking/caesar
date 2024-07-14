package com.caesar.entity.vo.request;

import com.caesar.enums.Environment;
import lombok.Data;

@Data
public class TaskExecuteVo {

    String taskName;
    int version;
    String environment;

}
