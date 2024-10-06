package com.caesar.model.code.model;

import lombok.Data;

import java.util.List;

@Data
public class ParamsConfig implements CodeModel {

    private String content;
    private List<CaesarParams> systemParams;
    private List<CaesarParams> engineParams;
    private List<CaesarParams> customParams;

}
