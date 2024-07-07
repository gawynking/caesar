package com.caesar.entity.vo;

import com.caesar.entity.BaseEntity;
import lombok.Data;

@Data
public class CaesarEngineVo extends BaseEntity {
    String engineType;
    String engineName;
    String engineVersion;
}
