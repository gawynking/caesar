package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarDatasource extends BaseEntity{

    String datasourceName;
    int datasourceType;
    int execEngine;
    String url;
    String username;
    String password;
    String dbName;
    int ownerId;

}
