package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
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
