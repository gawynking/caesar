package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTeamGroup;
import com.caesar.mapper.TeamGroupMapper;
import com.caesar.service.TeamGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TeamGroupServiceImpl extends ServiceImpl<TeamGroupMapper, CaesarTeamGroup> implements TeamGroupService {

    @Resource
    TeamGroupMapper teamGroupMapper;

    @Override
    public Boolean addTeamGroup(CaesarTeamGroup teamGroup) {
        return teamGroupMapper.addTeamGroup(teamGroup);
    }

    @Override
    public boolean deleteTeamGroup(int id) {
        return teamGroupMapper.deleteTeamGroup(id);
    }

}
