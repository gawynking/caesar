package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskReviewConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface TaskReviewConfigMapper extends BaseMapper<CaesarTaskReviewConfig> {

    @Select("select \n" +
            "\tcoalesce(t2.review_level,t1.review_level)  as review_level,\n" +
            "\tt2.review_user                             as review_user_id,\n" +
            "\tt2.username                                as review_username,\n" +
            "\tt2.review_desc                             as review_desc\n" +
            "from (\n" +
            "\tselect 1 as review_level \n" +
            "\tunion all \n" +
            "\tselect 2 as review_level \n" +
            "\tunion all \n" +
            "\tselect 3 as review_level\n" +
            ") t1 \n" +
            "left join (\n" +
            "\tselect t1.review_level,t1.review_user,t1.review_desc,t2.username \n" +
            "\tfrom caesar_task_review_config t1 \n" +
            "\tjoin caesar_user t2 on t1.review_user = t2.id \n" +
            "\twhere t1.group_id = #{groupId} \n" +
            "      and t1.task_type = #{taskType} \n" +
            ") t2 on t1.review_level = t2.review_level \n" +
            "where t2.review_level is not null" +
            "order by \n" +
            "\tt1.review_level")
    List<CaesarTaskReviewConfig> getCodeReviewConfig(int groupId, int taskType);
}
