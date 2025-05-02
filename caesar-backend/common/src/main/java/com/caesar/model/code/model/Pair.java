package com.caesar.model.code.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Pair implements CaesarParams{

    ParamsType type = ParamsType.Pair;
    String key;
    String value;
}
