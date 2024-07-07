package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarUserGroup;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserGroupMapper extends BaseMapper<CaesarUserGroup> {

    @Insert("insert into caesar_user_group(user_id,group_id) values (#{userId}, #{groupId})")
    Boolean addUserGroup(CaesarUserGroup userGroup);

}
