package com.caesar.scheduler.dolphin.model;


import com.alibaba.fastjson.JSONObject;
import com.caesar.util.JSONUtils;
import lombok.Data;

@Data
public class TaskRelation extends BaseModel{


    String name = "";
    long preTaskCode = 0l;
    int preTaskVersion = 0;
    long postTaskCode;
    int postTaskVersion = 0;
    String conditionType = "NONE";
    String conditionParams = "{}";


    @Override
    protected BaseModel cloneSelf() {
        try{
            return (TaskRelation)super.clone();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = JSONUtils.getJSONObject();
        jsonObject.put("name",name);
        jsonObject.put("preTaskCode",preTaskCode);
        jsonObject.put("preTaskVersion",preTaskVersion);
        jsonObject.put("postTaskCode",postTaskCode);
        jsonObject.put("postTaskVersion",postTaskVersion);
        jsonObject.put("conditionType",conditionType);
        jsonObject.put("conditionParams",conditionParams);
        return jsonObject;
    }
}
