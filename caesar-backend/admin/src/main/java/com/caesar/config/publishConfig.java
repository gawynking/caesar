package com.caesar.config;

import com.caesar.util.JSONUtils;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;


@Configuration
public class publishConfig {

    private static final Logger LOGGER = Logger.getLogger(publishConfig.class.getName());

//    private publishConfig(){}

    private static Map<String, Object> caesarConfig;

    static {
        LOGGER.info("Start Initializing Caesar Configuration");
        Yaml yaml = new Yaml();
        try {
            InputStream inputStream = publishConfig.class.getClassLoader().getResourceAsStream("config/application.yml");
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: config/application.yml");
            }
            caesarConfig = yaml.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 分发配置
        publishCommon();
        publishSchedule();

        LOGGER.info("Distribution of Caesar configuration ended");
    }

    private static void publishSchedule(){
        Map<String, Object> scheduleConfig = null;
        if (caesarConfig == null || !caesarConfig.containsKey("schedule")) {
            scheduleConfig = (Map<String, Object>)caesarConfig.get("schedule");
        }
        SchedulerConfig.setConfigMap(scheduleConfig);
    }

    private static void publishCommon(){
        Map<String, Object> config = null;
        if (caesarConfig == null) {
            Map<String, Object> tmpConfig = (Map<String, Object>)caesarConfig.get("spring");
            config = (Map<String, Object>) tmpConfig.get("datasource");
        }
        CommonConfig.setConfigMap(config);
    }


    public static void main(String[] args) {
        System.out.println(JSONUtils.getJSONObjectFromMap(caesarConfig));
        Map<String, Object> schedule = (Map<String, Object>) caesarConfig.get("schedule");
        System.out.println(schedule);
    }

}
