package com.caesar.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CaesarConfig {

    @Value("${schedule.category}")
    private String scheduleCategory;

    @Value("${schedule.system}")
    private String scheduleSystem;

    @Value("${schedule.base-url}")
    private String scheduleBaseUrl;

    @Value("${schedule.version}")
    private String scheduleVersion;

    @Value("${schedule.token}")
    private String scheduleToken;

    @Value("${schedule.level}")
    private String scheduleLevel;


    @Value("${schedule.sync-mode}")
    private String syncMode;



//    @Value("${schedule.projects}")
    private String scheduleProjects;

    @Value("${schedule.projects.hour}")
    private String scheduleProjectHour;
    @Value("${schedule.projects.day}")
    private String scheduleProjectDay;
    @Value("${schedule.projects.week}")
    private String scheduleProjectWeek;
    @Value("${schedule.projects.month}")
    private String scheduleProjectMonth;

}
