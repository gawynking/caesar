package com.caesar.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class CaesarMenu {

    int id;
    int location;
    int nodeType;
    int parentId;
    int menuType;
    String menuIndex;
    String menuName;
    Timestamp createTime;
    Timestamp updateTime;

}
