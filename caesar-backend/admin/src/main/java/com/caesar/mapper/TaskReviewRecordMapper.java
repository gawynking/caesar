package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.CaesarReviewTaskDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface TaskReviewRecordMapper extends BaseMapper<CaesarTaskReviewRecord> {

    @Select("select max(1) as is_submit \n" +
            "from caesar_task_review_record t1 \n" +
            "join (\n" +
            "\tselect max(id) as id \n" +
            "\tfrom caesar_task_review_record \n" +
            "\twhere task_id = #{taskId} \n" +
            ") t2 on t1.id = t2.id \n" +
            "where t1.task_id = #{taskId} \n" +
            "  and t1.review_status = 1 \n" +
            "  and t1.review_result = 0")
    Boolean taskAlreadySubmitted(int taskId);


    @Select("select \n" +
            "\tt1.id,\n" +
            "\tt1.task_id,\n" +
            "\tt1.task_name,\n" +
            "\tt1.task_version,\n" +
            "\tt1.pre_version,\n" +
            "\tt1.submit_username,\n" +
            "\tt1.code_desc,\n" +
            "\tt1.review_level,\n" +
            "\tt1.review_status,\n" +
            "\tt1.review_result,\n" +
            "\tt1.create_time,\n" +
            "\tt1.current_code,\n" +
            "\tt1.last_code \n" +
            "from (\n" +
            "\tselect \n" +
            "\t\tt1.id,\n" +
            "\t\tt1.task_id,\n" +
            "\t\tt1.task_name,\n" +
            "\t\tt1.task_version,\n" +
            "\t\tt1.pre_version,\n" +
            "\t\tt2.username as submit_username,\n" +
            "\t\tt1.code_desc,\n" +
            "\t\tt1.review_level,\n" +
            "\t\tt1.review_status,\n" +
            "\t\tt1.review_result,\n" +
            "\t\tt1.create_time,\n" +
            "\t\tt3.task_script as current_code,\n" +
            "\t\tt4.task_script as last_code \n" +
            "\tfrom caesar_task_review_record t1 \n" +
            "\tjoin caesar_user t2 on t1.submit_user_id = t2.id \n" +
            "\tjoin caesar_task t3 on t1.task_name = t3.task_name and t1.task_version = t3.version \n" +
            "\tjoin caesar_task t4 on t1.task_name = t4.task_name and t1.pre_version = t4.version \n" +
            "\twhere find_in_set(#{loginUserId},review_users)\n" +
            "\t  and review_result = 0 -- 处理中 \n" +
            "\t  and review_status = 1 -- 处理中 \n" +
            ") t1 \n" +
            "join caesar_task_review_record t2 \n" +
            "\t on true \n" +
            "\tand t2.review_result = 0 \n" +
            "\tand t2.review_status = 1 \n" +
            "group by \n" +
            "\tt1.id,\n" +

            "\tt1.task_id,\n" +
            "\tt1.task_name,\n" +
            "\tt1.task_version,\n" +
            "\tt1.pre_version,\n" +
            "\tt1.submit_username,\n" +
            "\tt1.code_desc,\n" +
            "\tt1.review_level,\n" +
            "\tt1.review_status,\n" +
            "\tt1.review_result,\n" +
            "\tt1.create_time,\n" +
            "\tt1.current_code,\n" +
            "\tt1.last_code\n" +
            "\t\n" +
            "having max(if(t2.review_level < t1.review_level,1,0)) != 1")
    List<CaesarReviewTaskDto> getReviewTaskListByUserId(int loginUserId);

    @Update("update caesar_task_review_record set review_status = #{reviewStatus},review_result=#{reviewResult},audit_message=#{auditMessage} where id=#{id}")
    Boolean review(int id, int taskId, int reviewStatus,int reviewResult, String auditMessage);

    @Select({
            "<script>",
            "SELECT * FROM (" +
            "select t1.* \n" +
                    "from caesar_task_review_record t1 \n" +
                    "join (\n" +
                    "\tselect task_name,max(update_time) as update_time  \n" +
                    "\tfrom caesar_task_review_record\n" +
                    "\tgroup by task_name \n" +
                    ") t2 on t1.task_name = t2.task_name and t1.update_time = t2.update_time" +
            ") t",
            "WHERE submit_user_id = #{loginUser} ",
            "<if test='taskName != null and taskName != \"\"'>",
            "   AND task_name LIKE CONCAT('%', #{taskName}, '%')",
            "</if>",
            "<if test='reviewStatus != null'>",
            "   AND review_status = #{reviewStatus}",
            "</if>",
            "<if test='reviewResult != null'>",
            "   AND review_result = #{reviewResult}",
            "</if>",
            "ORDER BY create_time DESC",
            "</script>"
    })
    List<CaesarTaskReviewRecord> getReviewTasks(
            @Param("loginUser") Integer loginUser,
            @Param("taskName") String taskName,
            @Param("reviewStatus") Integer reviewStatus,
            @Param("reviewResult") Integer reviewResult
    );
}

