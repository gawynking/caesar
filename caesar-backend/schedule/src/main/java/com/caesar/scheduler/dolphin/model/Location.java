package com.caesar.scheduler.dolphin.model;

import com.alibaba.fastjson.JSONObject;
import com.caesar.util.JSONUtils;
import lombok.Data;

import java.util.Random;

@Data
public class Location extends BaseModel{

    long taskCode;
    int x = new Random().nextInt(100);
    int y = new Random().nextInt(100);

    @Override
    protected BaseModel cloneSelf() {

        try{
            Location location = (Location)super.clone();
            location.x = new Random().nextInt(100);
            location.y = new Random().nextInt(500);
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
