package com.caesar.factory;

import com.caesar.enums.SchedulerEnum;
import com.caesar.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SchedulerFactoryManager {


    private static final Map<String, SchedulerFactory> factoryMap = new HashMap<>();

    public SchedulerFactoryManager(){
        this.register();
    }

    public void registerFactory(SchedulerEnum type, SchedulerFactory factory) {
        factoryMap.put(type.getKey(), factory);
    }

    public SchedulerFactory getEngineFactory(SchedulerEnum type) {
        return factoryMap.get(type.getKey());
    }

    public void register(){
        Set<Class<? extends SchedulerFactory>> factorys = ObjectUtils.findAllSubclasses(SchedulerFactory.class);
        for(Class<?> factory: factorys){
            try{
                SchedulerFactory instance = (SchedulerFactory) factory.getDeclaredConstructor().newInstance();
                this.registerFactory(instance.getSchedulerType(),instance);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
