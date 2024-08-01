package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskReviewRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface TaskReviewRecordMapper extends BaseMapper<CaesarTaskReviewRecord> {

    @Select("select 1 as is_submit \n" +
            "from caesar_task_review_record t1 \n" +
            "join (\n" +
            "\tselect max(id) as id\n" +
            "\tfrom caesar_task_review_record \n" +
            "\tgroup by task_id \n" +
            ") t2 on t1.id = t2.id \n" +
            "where t1.review_status = 1 -- 审核中")
    boolean taskAlreadySubmitted(int taskId);
}
