package com.caesar.scheduler.dolphin.model;

import com.alibaba.fastjson.JSONObject;
import com.caesar.util.ObjectUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public abstract class BaseModel implements Serializable,Cloneable {

    protected static volatile Map<String,BaseModel> modelMapper = new ConcurrentHashMap<>();
    static {
        register();
    }

    public static Map<String,BaseModel> getModelMapper(){
        return modelMapper;
    }

    public BaseModel getModel(){
        return cloneSelf();
    }

    protected abstract BaseModel cloneSelf();

    public abstract JSONObject toJSONObject();

    private static void register(){
        Set<Class<? extends BaseModel>> models = ObjectUtils.findAllSubclasses(BaseModel.class);
        for(Class<?> model: models){
            try{
                BaseModel instance = (BaseModel) model.getDeclaredConstructor().newInstance();
                modelMapper.put(model.getName(),instance);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
