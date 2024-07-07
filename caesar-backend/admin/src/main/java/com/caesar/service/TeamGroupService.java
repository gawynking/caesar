package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTeamGroup;

public interface TeamGroupService extends IService<CaesarTeamGroup> {

    Boolean addTeamGroup(CaesarTeamGroup teamGroup);

    boolean deleteTeamGroup(int id);
}
