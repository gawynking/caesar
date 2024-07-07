package com.caesar.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaesarMenuVo {
    int parentId;
    String menuIndex;
    String menuName;
    int location;
    int nodeType;
    int menuType;
    String extendProperties;
}
