package com.caesar.model.code.model;

import lombok.Data;

@Data
public class TaskContentModel implements CodeModel {
    MetaContent metaContent;
    ParamsConfig paramsConfig;
    SchemaDefine schemaDefine;
    EtlProcess etlProcess;
}
