package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarMenuRolePermission {
    int id;
    int roleId;
    int menuId;
    Timestamp createTime;
    Timestamp updateTime;
}
