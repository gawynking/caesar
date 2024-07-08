package com.caesar.entity;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CaesarMenu extends BaseEntity{

    int parentId;
    String menuIndex;
    String menuName;
    int location;
    int nodeType;
    int menuType;
    String extendProperties;

}
