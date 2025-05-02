package com.caesar.config;

import com.caesar.constant.EngineConfig;
import com.caesar.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;


@Component
@Configuration
public class publishConfig {

    private static final Logger LOGGER = Logger.getLogger(publishConfig.class.getName());


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
            LOGGER.info("Caesar full Configuration info: " + caesarConfig);
        } catch (Exception e) {
            LOGGER.warning("Scheduler Initializing faild");
            e.printStackTrace();
        }

        // 分发配置
        publishCommon();
        publishEngine();
        publishSchedule();

        LOGGER.info("Distribution of Caesar configuration ended");
    }


    private static void publishCommon(){
        Map<String, Object> config = null;
        if (null != caesarConfig && caesarConfig.containsKey("spring")) {
            Map<String, Object> tmpConfig = (Map<String, Object>)caesarConfig.get("spring");
            if(tmpConfig.containsKey("datasource")){
                config = (Map<String, Object>) tmpConfig.get("datasource");
            }
        }
        CommonConfig.setConfigMap(config);
        LOGGER.info("Common Configuration: " + config);
    }


    private static void publishEngine(){
        Map<String, Object> engineConfig = null;
        if (null != caesarConfig && caesarConfig.containsKey("engine")) {
            engineConfig = (Map<String, Object>)caesarConfig.get("engine");
        }

        // 加入资源
        Map<String, String> shellMapping = ShellMapping.getShellMapping();
        for(String shell:shellMapping.keySet()){
            String shellScript = "";
            Resource sparkResource = new ClassPathResource(shellMapping.get(shell));
            try {
                shellScript = IOUtils.toString(sparkResource.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            engineConfig.put(shell.toLowerCase().trim(),shellScript);
        }

        EngineConfig.setConfigMap(engineConfig);
        LOGGER.info("Engine Configuration: " + engineConfig);
    }


    private static void publishSchedule(){
        Map<String, Object> scheduleConfig = null;
        if (null != caesarConfig && caesarConfig.containsKey("schedule")) {
            scheduleConfig = (Map<String, Object>)caesarConfig.get("schedule");
        }
        SchedulerConfig.setConfigMap(scheduleConfig);
        LOGGER.info("Schedule Configuration: " + scheduleConfig);
    }

    public Map<String, Object> getCaesarConfig(){
        return caesarConfig;
    }

    public static void main(String[] args) {
        System.out.println(JSONUtils.getJSONObjectFromMap(caesarConfig));
        Map<String, Object> schedule = (Map<String, Object>) caesarConfig.get("schedule");
        System.out.println(schedule);
    }

}
