package com.caesar.scheduler.dolphin.process;

import com.caesar.config.SchedulerConfig;

import java.lang.reflect.Constructor;

public class DolphinSchedulerAPIFactory {


    public static DolphinSchedulerAPI getDolphinSchedulerAPI(){

        String version = SchedulerConfig.VERSION;
        String baseUrl = SchedulerConfig.BASE_URL;
        String token = SchedulerConfig.TOKEN;
        Object[] params = new Object[] { baseUrl, token };

        Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
        String className = "com.caesar.scheduler.dolphin.process.DolphinSchedulerAPI"+version.replaceAll("\\.","").replaceAll("\\s","");
        try{
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(paramTypes);
            return (DolphinSchedulerAPI) constructor.newInstance(params);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
