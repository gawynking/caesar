package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarDatasource extends BaseEntity{

    String datasourceName;
    int datasourceType;
    int engine;
    String url;
    String username;
    String password;
    String dbName;
    int ownerId;

}
