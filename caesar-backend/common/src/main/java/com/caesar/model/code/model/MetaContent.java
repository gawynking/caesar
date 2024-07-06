package com.caesar.model.code.model;

import com.caesar.enums.EngineEnum;
import com.caesar.enums.ExecuteEngineEnum;
import lombok.Data;

@Data
public class MetaContent implements CodeModel {

    String content;
    String author;
    String createTime;
    String taskName;
    EngineEnum engine;

}
