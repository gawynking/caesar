package com.caesar.entity;

import lombok.Data;

@Data
public class CaesarTaskParameter extends BaseEntity{

    String paramName;
    String paramDesc;
    String expression;

}
