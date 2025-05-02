package com.caesar.model.code.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParamsModel {

    List<String> systemParams = new ArrayList<>();
    List<String> engineParams = new ArrayList<>();
    List<String> customParams = new ArrayList<>();

    public void addSystemParams(String params){
        this.systemParams.add(params);
    }

    public void addEngineParams(String params){
        this.engineParams.add(params);
    }

    public void addCustomParams(String params){
        this.customParams.add(params);
    }

}
