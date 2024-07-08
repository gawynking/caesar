package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarGroupService;
import com.caesar.entity.dto.CaesarGroupServiceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupServiceMapper extends BaseMapper<CaesarGroupService> {

    @Select("select \n" +
            "\tt1.group_id,\n" +
            "\tt2.group_name,\n" +
            "\tt1.id,\n" +
            "\tt1.service_tag,\n" +
            "\tt1.level_tag,\n" +
            "\tt1.is_test\n" +
            "from caesar_group_service t1 \n" +
            "join caesar_team_group t2 on t1.group_id = t2.id")
    List<CaesarGroupServiceDto> getDbs();

}
