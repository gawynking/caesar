package com.caesar.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarMenuRolePermission extends BaseEntity{

    int roleId;
    int menuId;
    int permission;

}
