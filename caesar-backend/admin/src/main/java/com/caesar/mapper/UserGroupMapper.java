package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserGroupVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserGroupMapper extends BaseMapper<CaesarUserGroup> {

    @Insert("insert into caesar_user_group(user_id,group_id) values (#{userId}, #{groupId})")
    Boolean addUserGroup(CaesarUserGroup userGroup);

    @Delete("delete from caesar_user_group where user_id = #{userId}")
    boolean deleteUser(int userId);

    @Select("select t1.user_id,t1.group_id,t2.group_name \n" +
            "from caesar_user_group t1 \n" +
            "join caesar_team_group t2 on t1.group_id = t2.id ")
    List<CaesarUserGroupVo> getAllGroups();

    @Select("select * from caesar_user_group where group_id = #{groupId}")
    List<CaesarUserGroup> getUserGroupsByGroupId(int groupId);
}
