package com.caesar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CaesarConfig {

    public static final Map<String, String> configMap = new HashMap<>();


    @Value("engines.none.code-dir")
    public String noneCodedir;



    @Value("${engines.mysql.test.driver-class-name}")
    public String mysqlTestJdbcDriver;

    @Value("${engines.mysql.test.url}")
    public String mysqlTestJdbcUrl;

    @Value("${engines.mysql.test.username}")
    public String mysqlTestUsername;

    @Value("${engines.mysql.test.password}")
    public String mysqlTestPassword;



    @Value("${engines.mysql.staging.driver-class-name}")
    public String mysqlStagingJdbcDriver;

    @Value("${engines.mysql.staging.url}")
    public String mysqlStagingJdbcUrl;

    @Value("${engines.mysql.staging.username}")
    public String mysqlStagingUsername;

    @Value("${engines.mysql.staging.password}")
    public String mysqlStagingPassword;



    @Value("${engines.mysql.production.driver-class-name}")
    public String mysqlProductionJdbcDriver;

    @Value("${engines.mysql.production.url}")
    public String mysqlProductionJdbcUrl;

    @Value("${engines.mysql.production.username}")
    public String mysqlProductionUsername;

    @Value("${engines.mysql.production.password}")
    public String mysqlProductionPassword;



    public Map<String, String> getConfigMap() {
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Value.class)) {
                field.setAccessible(true);
                try {
                    String key = field.getAnnotation(Value.class).value();
                    key = key.replace("${", "").replace("}", "");
                    String value = (String) field.get(this);
                    configMap.put(key, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return configMap;
    }

}
