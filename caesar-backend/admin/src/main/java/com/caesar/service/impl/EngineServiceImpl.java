package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarEngine;
import com.caesar.entity.vo.CaesarEngineVo;
import com.caesar.mapper.EngineMapper;
import com.caesar.service.EngineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EngineServiceImpl extends ServiceImpl<EngineMapper, CaesarEngine> implements EngineService {

    @Resource
    EngineMapper engineMapper;


    @Override
    public List<CaesarEngineVo> getEngines() {
        return engineMapper.getEngines();
    }

}
