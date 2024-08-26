package com.caesar.scheduler.dolphin.model;


import com.alibaba.fastjson.JSONObject;
import com.caesar.util.JSONUtils;
import lombok.Data;

@Deprecated
@Data
public class TaskDefinitionModel extends BaseModel {

    ShellModel taskModel = new ShellModel();
    DependenceModel dependenceModel = new DependenceModel();

    @Override
    protected BaseModel cloneSelf() {
        TaskDefinitionModel taskDefinitionModel = new TaskDefinitionModel();
        taskDefinitionModel.taskModel = new ShellModel();
        taskDefinitionModel.dependenceModel = new DependenceModel();
        return taskDefinitionModel;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = JSONUtils.getJSONObject();
        jsonObject.put("taskModel",taskModel.toJSONObject());
        jsonObject.put("dependenceModel",dependenceModel.toJSONObject());
        return jsonObject;
    }

}
