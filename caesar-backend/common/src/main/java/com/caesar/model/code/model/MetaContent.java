package com.caesar.model.code.model;

import com.caesar.enums.EngineEnum;
import lombok.Data;

@Data
public class MetaContent implements CodeModel {

    String content;
    String author;
    String createTime;
    String groupName;
    String taskName;
    EngineEnum engine;

}
