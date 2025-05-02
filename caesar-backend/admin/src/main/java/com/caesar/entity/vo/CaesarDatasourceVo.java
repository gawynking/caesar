package com.caesar.entity.vo;

import lombok.Data;

@Data
public class CaesarDatasourceVo {

    String datasourceName;
    int datasourceType;
    int engine;
    String datasourceInfo;
    String ownerName;

}
