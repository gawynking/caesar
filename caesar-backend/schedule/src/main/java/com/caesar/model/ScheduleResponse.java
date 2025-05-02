package com.caesar.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;


@Data
public class ScheduleResponse {

    public Integer code;
    public String message;
    public Map<String,JSONObject> data;


    public ScheduleResponse success(String opType, JSONObject result){
        this.code = 200;
        this.message = "success";
        if(null == this.data){
            this.data = new HashMap<>();
            this.data.put(opType,result);
        }else{
            this.data.put(opType,result);
        }
        return this;
    }


    public ScheduleResponse faild(String opType, JSONObject result){
        this.code = 500;
        this.message = "faild";
        if(null == this.data){
            this.data = new HashMap<>();
            this.data.put(opType,result);
        }else{
            this.data.put(opType,result);
        }
        return this;
    }


    public ScheduleResponse faild(String opType){
        this.code = 500;
        this.message = "faild";
        if(null == this.data){
            this.data = new HashMap<>();
        }else{
        }
        return this;
    }

}
