package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskExecuteRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface TaskExecuteMapper extends BaseMapper<CaesarTaskExecuteRecord> {

    @Select("select id from caesar_task_execute_record where task_id = #{taskId} and environment = #{environment} and is_success = 0 order by begin_time desc limit 1")
    int findIdFromNow(CaesarTaskExecuteRecord taskExecuteRecord);

    @Select("select max(id) as id from caesar_task_execute_record where task_id = #{taskId} and environment = 'test' and is_success = 1")
    Integer checkTaskExecuteTested(CaesarTaskExecuteRecord taskExecuteRecord);

    @Update("update caesar_task_execute_record set is_success=#{isSuccess},end_time=#{endTime} where id=#{id}")
    boolean updateExecuteState(int isSuccess, LocalDateTime endTime, int id);

    @Select("select id from caesar_task_execute_record where task_id = #{taskId} and environment = #{environment} and uuid = #{uuid}")
    int findIdFromUUID(CaesarTaskExecuteRecord taskExecuteRecord);

    @Select("select max(1) as is_tested \n" +
            "from caesar_task_execute_record \n" +
            "where task_id = #{taskId} \n" +
            "  and is_success = 1")
    Boolean validateTaskIsPassedTest(int taskId);

    @Select("select * from caesar_task_execute_record where plan_uuid = #{planUuid} and is_success = 1")
    List<CaesarTaskExecuteRecord> findRecordByPlan(String planUuid);
}
