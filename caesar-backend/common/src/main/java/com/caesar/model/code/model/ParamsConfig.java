package com.caesar.model.code.model;

import lombok.Data;

import java.util.List;

@Data
public class ParamsConfig implements CodeModel {

    private String content;
    private List<Pair> systemParams;
    private List<String> engineParams;
    private List<String> customParams;


}
