package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarScheduleDependency;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleDependencyMapper extends BaseMapper<CaesarScheduleDependency> {

    @Select("select t1.* \n" +
            "from caesar_schedule_dependency t1 \n" +
            "join (\n" +
            "\tselect \n" +
            "\t\tschedule_code,\n" +
            "\t\tmax(version) as version \n" +
            "\tfrom caesar_schedule_config \n" +
            "\twhere task_name = #{taskName}\n" +
            "\tgroup by schedule_code \n" +
            ") t2 \n" +
            "   on t1.schedule_code = t2.schedule_code")
    List<CaesarScheduleDependency> getTaskScheduleDependencys(String taskName);

    @Insert("insert into caesar_schedule_dependency(\n" +
            "\tschedule_code,\n" +
            "\tpre_schedule_code,\n" +
            "\tjoin_type,\n" +
            "\towner_id\n" +
            ")values(\n" +
            "\t#{scheduleCode},\n" +
            "\t#{preScheduleCode},\n" +
            "\t#{joinType},\n" +
            "\t#{ownerId}\n" +
            ")")
    Boolean saveTaskDependency(CaesarScheduleDependency dependency);


    @Select("select * from caesar_schedule_dependency where schedule_code = #{scheduleCode}")
    List<CaesarScheduleDependency> getTaskScheduleDependencyFromScheduleCode(String scheduleCode);

    @Delete("delete from caesar_schedule_dependency where schedule_code = #{scheduleCode}")
    Boolean deleteByScheduleCode(String scheduleCode);

    @Select("select max(1) from caesar_schedule_dependency where schedule_code = #{scheduleCode} and pre_schedule_code = #{preScheduleCode}")
    CaesarScheduleDependency findTaskDependency(String scheduleCode, String preScheduleCode);

    @Select("select t1.* \n" +
            "from caesar_schedule_dependency t1 \n" +
            "join (\n" +
            "\tselect schedule_code,max(version) as version \n" +
            "\tfrom caesar_schedule_config \n" +
            "\twhere schedule_name = #{scheduleName}\n" +
            "\tgroup by schedule_code \n" +
            ") t2 on t1.schedule_code = t2.schdule_code")
    List<CaesarScheduleDependency> getTaskScheduleDependency(String scheduleName);

    @Update("update caesar_schedule_dependency set join_type = #{joinType},owner_id = #{ownerId} where schedule_code = #{scheduleCode} and pre_schedule_code = #{preScheduleCode}")
    Boolean updateTaskDependency(CaesarScheduleDependency scheduleDependency);
}
