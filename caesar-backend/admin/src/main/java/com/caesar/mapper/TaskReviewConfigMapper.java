package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskReviewConfig;
import com.caesar.entity.dto.CaesarGroupReviewConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface TaskReviewConfigMapper extends BaseMapper<CaesarTaskReviewConfig> {

    @Select("select t1.review_level,max(t1.review_desc) as review_desc, GROUP_CONCAT(distinct concat(t1.review_user,'|',t2.username)) as review_users \n" +
            "from caesar_task_review_config t1 \n" +
            "join caesar_user t2 on t1.review_user = t2.id \n" +
            "where t1.group_id = #{groupId} \n" +
            "  and t1.task_type = #{taskType} \n" +
            "group by \n" +
            "\tt1.review_level")
    List<CaesarGroupReviewConfig> getCodeReviewConfig(int groupId, int taskType);
}
