package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<CaesarUser> {

    @Select("select t2.group_id as group_id \n" +
            "from caesar_user t1 \n" +
            "join caesar_user_group t2 on t1.id = t2.user_id \n" +
            "where t1.id = #{id}")
    List<Integer> getTeamGroupsFromId(int id);


    @Select("select id as userId from caesar_user where username = #{userName}")
    Integer getUserIdFromUserName(String userName);
}
