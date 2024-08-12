package com.caesar.scheduler.dolphin.model;

import com.alibaba.fastjson.JSONObject;
import com.caesar.util.JSONUtils;
import lombok.Data;

@Data
public class Location extends BaseModel{

    long taskCode;
    int x = 100;
    int y = 50;

    @Override
    protected BaseModel cloneSelf() {

        try{
            Location location = (Location)super.clone();
            return location;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = JSONUtils.getJSONObject();
        jsonObject.put("taskCode",taskCode);
        jsonObject.put("x",x);
        jsonObject.put("y",y);
        return jsonObject;
    }

}
