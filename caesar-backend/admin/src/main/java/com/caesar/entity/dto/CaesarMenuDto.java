package com.caesar.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaesarMenuDto {
    int location;
    int nodeType;
    int parentId;
    int menuType;
    String menuIndex;
    String menuName;
}
