package com.caesar.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class CaesarConfig {

    @Value("${schedule.category}")
    private String scheduleCategory;

    @Value("${schedule.system}")
    private String scheduleSystem;

    @Value("${schedule.project}")
    private String scheduleProject;

    @Value("${schedule.base-url}")
    private String scheduleBaseUrl;

    @Value("${schedule.version}")
    private String scheduleVersion;

    @Value("${schedule.token}")
    private String scheduleToken;

    @Value("${schedule.level}")
    private String scheduleLevel;

}
