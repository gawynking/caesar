package com.caesar.model.code.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CustomFunctionParams implements CaesarParams{

    ParamsType type = ParamsType.customFunction;
    String statement;

}
