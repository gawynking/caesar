package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarUser;
import com.caesar.entity.vo.CaesarUserVo;
import org.apache.ibatis.annotations.*;

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

    @Select("select team_group from caesar_user where id = #{id}")
    List<Integer> getTeamGroups(int id);

    @Select("select username from caesar_user where id = #{userId}")
    String getUsernameFromId(int userId);

    @Select("select * from caesar_user where username = #{username} and is_activated = 1")
    CaesarUser findByUsername(String username);

    @Insert("insert into caesar_user(\n" +
            "\tusername,\n" +
            "\tpassword,\n" +
            "\temail,\n" +
            "\tphone\n" +
            ")values(\n" +
            "\t#{username},\n" +
            "\t#{password},\n" +
            "\t#{email},\n" +
            "\t#{phone}\n" +
            ");")
    boolean save(CaesarUser user);

    @Update("update caesar_user set is_activated = 1 where id = #{id}")
    Boolean activatedUser(int id);

    @Delete("delete from caesar_user where id = #{id}")
    boolean deleteUser(int id);

    @Select("select * from caesar_user")
    List<CaesarUserVo> getUserList();
}
