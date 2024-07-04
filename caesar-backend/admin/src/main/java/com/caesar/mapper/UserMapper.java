package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<CaesarUser> {

    @Select("select team_group from caesar_user where id = #{id}")
    int getTeamGroup(int id);


    @Select("select id as userId from caesar_user where username = #{userName}")
    Integer getUserIdFromUserName(String userName);
}
