package com.caesar.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaesarMenuDto {

    int parentId;
    String menuIndex;
    String menuName;
    int location;
    int nodeType;
    int menuType;

}
