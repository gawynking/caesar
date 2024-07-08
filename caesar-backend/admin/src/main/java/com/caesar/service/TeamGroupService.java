package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTeamGroup;
import com.caesar.entity.vo.CaesarTeamGroupVo;

import java.util.List;

public interface TeamGroupService extends IService<CaesarTeamGroup> {

    Boolean addTeamGroup(CaesarTeamGroup teamGroup);

    boolean deleteTeamGroup(int id);

    List<CaesarTeamGroupVo> getTeamList();
}
