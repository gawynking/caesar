package com.caesar.config;


import java.util.Map;

public class SchedulerConfig {

    private static Map<String, ?> configMap;

    public static void setConfigMap(Map<String,?> config){
        configMap = config;
    }

    public static Map<String, ?> getConfigMap(){
        return configMap;
    }

    public static Map<String, ?> getScheduleProperties() {
        return configMap;
    }

    public static String getString(String key) {
        Map<String, ?> scheduleProps = getScheduleProperties();
        if (scheduleProps != null && scheduleProps.containsKey(key)) {
            return (String) scheduleProps.get(key);
        }
        return null;
    }

    public static Integer getInteger(String key) {
        Map<String, ?> scheduleProps = getScheduleProperties();
        if (scheduleProps != null && scheduleProps.containsKey(key)) {
            return (Integer) scheduleProps.get(key);
        }
        return 0;
    }

    public static Boolean getBoolean(String key) {
        Map<String, ?> scheduleProps = getScheduleProperties();
        if (scheduleProps != null && scheduleProps.containsKey(key)) {
            return (Boolean) scheduleProps.get(key);
        }
        return false;
    }

    public static Long getLong(String key) {
        Map<String, ?> scheduleProps = getScheduleProperties();
        if (scheduleProps != null && scheduleProps.containsKey(key)) {
            return (Long) scheduleProps.get(key);
        }
        return 0L;
    }

}