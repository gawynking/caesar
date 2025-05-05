package com.caesar.entity;

import lombok.Data;


@Data
public class CaesarTaskExecutePlan extends BaseEntity{

    String uuid;
    int taskId;
    String taskName;
    int taskVersion;
    String environment;
    String period;
    String startDate;
    String endDate;
    int status;

}

