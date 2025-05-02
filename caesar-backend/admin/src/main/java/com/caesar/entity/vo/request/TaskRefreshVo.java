package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class TaskRefreshVo {

    String taskName;
    int version;
    String environment;
    String period;
    String startDate;
    String endDate;

}
