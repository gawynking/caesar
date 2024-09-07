package com.caesar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CaesarConfig {

    public static final Map<String, String> configMap = new HashMap<>();


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
