package com.caesar.factory;

import com.caesar.enums.EngineEnum;
import com.caesar.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EngineFactoryRegistry {

    private static final Map<String, EngineFactory> factoryMap = new HashMap<>();

    public EngineFactoryRegistry(){
        this.register();
    }

    public void registerFactory(EngineEnum type, EngineFactory factory) {
        factoryMap.put(type.getEngine(), factory);
    }

    public EngineFactory getEngineFactory(EngineEnum type) {
        return factoryMap.get(type.getEngine());
    }

    public void register(){
        Set<Class<? extends EngineFactory>> factorys = ObjectUtils.findAllSubclasses(EngineFactory.class);
        for(Class<?> factory: factorys){
            try{
                EngineFactory instance = (EngineFactory) factory.getDeclaredConstructor().newInstance();
                this.registerFactory(instance.getEngineEnum(),instance);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
