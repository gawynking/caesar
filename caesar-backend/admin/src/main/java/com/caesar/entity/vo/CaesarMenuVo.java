package com.caesar.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaesarMenuVo {
    int location;
    int nodeType;
    int parentId;
    int menuType;
    String menuIndex;
    String menuName;
}
