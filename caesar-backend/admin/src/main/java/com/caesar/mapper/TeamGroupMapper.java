package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTeamGroup;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeamGroupMapper extends BaseMapper<CaesarTeamGroup> {

    @Insert("insert into caesar_team_group(group_name, group_desc, owner_id) values (#{groupName}, #{groupDesc}, #{ownerId})")
    Boolean addTeamGroup(CaesarTeamGroup teamGroup);

    @Delete("delete from caesar_team_group where id = #{id}")
    boolean deleteTeamGroup(int id);

    @Select("select group_name from caesar_team_group where id = #{id}")
    String getGroupNameFromId(int id);
}
