package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarEngine extends BaseEntity{

    String engineType;
    String engineName;
    String engineVersion;
    int isActivated;

}
