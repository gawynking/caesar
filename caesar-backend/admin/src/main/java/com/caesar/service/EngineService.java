package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarEngine;
import com.caesar.entity.vo.CaesarEngineVo;

import java.util.List;

public interface EngineService extends IService<CaesarEngine> {


    List<CaesarEngineVo> getEngines();
}