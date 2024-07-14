package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarGroupService;
import com.caesar.entity.dto.CaesarGroupServiceDto;

import java.util.List;

public interface GroupServiceService extends IService<CaesarGroupService> {


    List<CaesarGroupServiceDto> getDbs();
}