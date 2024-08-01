package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class TaskPublishVo {

    String taskName;
    int version;
    String submitUsername;
    String codeDesc;

}
