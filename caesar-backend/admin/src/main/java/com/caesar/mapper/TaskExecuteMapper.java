package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskExecuteRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;


@Mapper
public interface TaskExecuteMapper extends BaseMapper<CaesarTaskExecuteRecord> {

    @Select("select id from caesar_task_execute_record where task_id = #{taskId} and environment = #{environment} and is_success = 0 order by begin_time desc limit 1")
    int findIdFromNow(CaesarTaskExecuteRecord taskExecuteRecord);

    @Select("select max(id) as id from caesar_task_execute_record where task_id = #{taskId} and environment = 'test' and is_success = 1")
    int checkTaskExecuteTest(CaesarTaskExecuteRecord taskExecuteRecord);

    @Select("select max(id) as id from caesar_task_execute_record where task_id = #{taskId} and environment = 'staging' and is_success = 1")
    Integer checkTaskExecuteTestAndStage(CaesarTaskExecuteRecord taskExecuteRecord);

    @Update("update caesar_task_execute_record set is_success=#{isSuccess},end_time=#{endTime} where id=#{id}")
    boolean updateExecuteState(int isSuccess, LocalDateTime endTime, int id);

    @Select("select id from caesar_task_execute_record where task_id = #{taskId} and environment = #{environment} and uuid = #{uuid}")
    int findIdFromUUID(CaesarTaskExecuteRecord taskExecuteRecord);
}
