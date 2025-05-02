package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskTemplateMapper extends BaseMapper<CaesarTaskTemplate> {

    @Select("select task_script " +
            "from caesar_task_template " +
            "where task_type = #{taskType} " +
            "  and owner_id = #{createdUser} " +
            "  and is_default = 1")
    String getTaskTemplateScriptFromOwnerAndTasktype(int createdUser, int taskType);
}
