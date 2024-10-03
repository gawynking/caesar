package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTeamGroup;
import com.caesar.entity.vo.CaesarTeamGroupVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamGroupMapper extends BaseMapper<CaesarTeamGroup> {

    @Insert("insert into caesar_team_group(group_name, group_desc, owner_id) values (#{groupName}, #{groupDesc}, #{ownerId})")
    Boolean addTeamGroup(CaesarTeamGroup teamGroup);

    @Delete("delete from caesar_team_group where id = #{id}")
    boolean deleteTeamGroup(int id);

    @Select("select group_name from caesar_team_group where id = #{id}")
    String getGroupNameFromId(int id);

    @Select("select \n" +
            "\tt1.id as id,\n" +
            "\tt2.id as owner_id,\n" +
            "\tt2.username as owner_name,\n" +
            "\tt1.group_name as group_name,\n" +
            "\tt1.group_desc as group_desc\n" +
            "from caesar_team_group t1 \n" +
            "join caesar_user t2 on t1.owner_id = t2.id ")
    List<CaesarTeamGroupVo> getTeamList();
}
