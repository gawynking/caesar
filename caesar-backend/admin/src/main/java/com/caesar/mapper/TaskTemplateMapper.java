package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskTemplateMapper extends BaseMapper<CaesarTaskTemplate> {

    @Select("select task_script \n" +
            "from caesar_task_template \n" +
            "where task_type = #{taskType}\n" +
            "  and owner_id = #{createdUser}\n" +
            "  and is_default = 1")
    String getTaskTemplateScript(int createdUser, int taskType);
}
