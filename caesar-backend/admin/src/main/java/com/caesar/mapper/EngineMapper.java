package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarEngine;
import com.caesar.entity.vo.CaesarEngineVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EngineMapper extends BaseMapper<CaesarEngine> {


    @Select("select id,engine_type,engine_name,engine_version from caesar_engine where is_activated = 1")
    List<CaesarEngineVo> getEngines();

}
