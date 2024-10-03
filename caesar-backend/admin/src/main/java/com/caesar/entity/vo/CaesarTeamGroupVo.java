package com.caesar.entity.vo;

import lombok.Data;

@Data
public class CaesarTeamGroupVo {
    int id;
    String groupName;
    String groupDesc;
    int ownerId;
    String ownerName;
}
