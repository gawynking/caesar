package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarScheduleCluster;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.vo.CaesarTaskParameterVo;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.model.MenuModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleClusterMapper extends BaseMapper<CaesarScheduleCluster> {

}
