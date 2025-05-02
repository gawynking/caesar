package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarGroupService;
import com.caesar.entity.dto.CaesarGroupServiceDto;
import com.caesar.entity.vo.response.MenuDbs;
import com.caesar.mapper.GroupServiceMapper;
import com.caesar.service.GroupServiceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GroupServiceServiceImpl extends ServiceImpl<GroupServiceMapper, CaesarGroupService> implements GroupServiceService {

    @Resource
    GroupServiceMapper groupServiceMapper;


    @Override
    public List<CaesarGroupServiceDto> getDbs() {
        return groupServiceMapper.getDbs();
    }

    @Override
    public List<MenuDbs> getMenuDbs() {
        return groupServiceMapper.getMenuDbs();
    }
}
