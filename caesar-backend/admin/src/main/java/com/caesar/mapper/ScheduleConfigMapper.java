package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarScheduleConfig;
import com.caesar.entity.CaesarScheduleDependency;
import com.caesar.entity.bo.CaesarScheduleConfigInfoBo;
import com.caesar.entity.dto.CaesarScheduleConfigDto;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface ScheduleConfigMapper extends BaseMapper<CaesarScheduleConfig> {


    @Select("select max(version) as version from caesar_schedule_config")
    Integer getVersion();

    @Select("select t1.* \n" +
            "from caesar_schedule_config t1 \n" +
            "join (\n" +
            "\tselect \n" +
            "\t\tschedule_code,\n" +
            "\t\tmax(version) as version \n" +
            "\tfrom caesar_schedule_config \n" +
            "\twhere task_name = #{taskName}\n" +
            "\tgroup by schedule_code \n" +
            ") t2 \n" +
            "   on t1.schedule_code = t2.schedule_code \n" +
            "  and t1.version = t2.version \n" +
            "where t1.task_name = #{taskName}")
    List<CaesarScheduleConfigDto> getTaskSchedules(String taskName);

    @Insert("insert into caesar_schedule_config(\n" +
            "\ttask_id,\n" +
            "\ttask_name,\n" +
            "\ttask_version,\n" +
            "\tschedule_category,\n" +
            "\tschedule_level,\n" +
            "\tproject,\n" +
            "\tschedule_code,\n" +
            "\tschedule_name,\n" +
            "\trelease_status,\n" +
            "\ttask_type,\n" +
            "\tschedule_params,\n" +
            "\ttask_priority,\n" +
            "\tfail_retry_times,\n" +
            "\tfail_retry_interval,\n" +
            "\tbegin_time,\n" +
            "\towner_id,\n" +
            "\tversion,\n" +
            "\tperiod,\n" +
            "\tdate_value\n" +
            ")values(\n" +
            "\t#{taskId},\n" +
            "\t#{taskName},\n" +
            "\t#{taskVersion},\n" +
            "\t#{scheduleCategory},\n" +
            "\t#{scheduleLevel},\n" +
            "\t#{project},\n" +
            "\t#{scheduleCode},\n" +
            "\t#{scheduleName},\n" +
            "\t#{releaseStatus},\n" +
            "\t#{taskType},\n" +
            "\t#{scheduleParams},\n" +
            "\t#{taskPriority},\n" +
            "\t#{failRetryTimes},\n" +
            "\t#{failRetryInterval},\n" +
            "\t#{beginTime},\n" +
            "\t#{ownerId},\n" +
            "\t#{version},\n" +
            "\t#{period},\n" +
            "\t#{dateValue}\n" +
            ");")
    Boolean genTaskSchedule(CaesarScheduleConfig scheduleConfig);


    @Update("update caesar_schedule_config\n" +
            "set \n" +
            "task_id = #{taskId},\n" +
            "task_name = #{taskName},\n" +
            "task_version=#{taskVersion},\n" +
            "schedule_category = #{scheduleCategory},\n" +
            "schedule_level = #{scheduleLevel},\n" +
            "project = #{project},\n" +
            "schedule_code = #{scheduleCode},\n" +
            "schedule_name = #{scheduleName},\n" +
            "release_status = #{releaseStatus},\n" +
            "task_type = #{taskType},\n" +
            "schedule_params = #{scheduleParams},\n" +
            "task_priority = #{taskPriority},\n" +
            "fail_retry_times = #{failRetryTimes},\n" +
            "fail_retry_interval = #{failRetryInterval},\n" +
            "begin_time = #{beginTime},\n" +
            "owner_id = #{ownerId},\n" +
            "version = #{version},\n" +
            "period=#{period},\n" +
            "date_value=#{dateValue}\n" +
            "where schedule_name = #{scheduleName}")
    Boolean updateTaskSchedule(CaesarScheduleConfig scheduleConfig);


    @Select("select max(schedule_code) as schedule_code from caesar_schedule_config where schedule_name=#{scheduleName}")
    String getTaskScheduleCodeFromScheduleName(String scheduleName);

    @Delete("delete from caesar_schedule_config where schedule_code = #{scheduleCode}")
    Boolean deleteByScheduleCode(String scheduleCode);

    @Select("select t1.* \n" +
            "from caesar_schedule_config t1 \n" +
            "join (\n" +
            "\tselect max(version) as version \n" +
            "\tfrom caesar_schedule_config \n" +
            "\twhere schedule_name = #{scheduleName}\n" +
            ") t2 on t1.version = t2.version \n" +
            "where t1.schedule_name = #{scheduleName}")
    CaesarScheduleConfigDto getTaskSchedule(String scheduleName);

    @Select("select max(schedule_name) as schedule_name from caesar_schedule_config where schedule_code = #{scheduleCode}")
    String getScheduleNameFromScheduleCode(String preScheduleCode);

    @Select("select t1.* \n" +
            "from caesar_schedule_config t1 \n" +
            "where t1.task_name like #{taskName}\n" +
            "  and t1.period = #{period}\n" +
            "  and t1.release_status = 1")
    List<CaesarScheduleConfigDto> findTaskScheduleConfigListFromTaskNameAndPeriod(String taskName, String period);


    @Select("select * from caesar_schedule_config")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "taskId", column = "task_id"),
            @Result(property = "taskName", column = "task_name"),
            @Result(property = "taskVersion", column = "task_version"),
            @Result(property = "scheduleCategory", column = "schedule_category"),
            @Result(property = "scheduleLevel", column = "schedule_level"),
            @Result(property = "project", column = "project"),
            @Result(property = "scheduleCode", column = "schedule_code"),
            @Result(property = "scheduleName", column = "schedule_name"),
            @Result(property = "releaseStatus", column = "release_status"),
            @Result(property = "taskType", column = "task_type"),
            @Result(property = "scheduleParams", column = "schedule_params"),
            @Result(property = "taskPriority", column = "task_priority"),
            @Result(property = "failRetryTimes", column = "fail_retry_times"),
            @Result(property = "failRetryInterval", column = "fail_retry_interval"),
            @Result(property = "beginTime", column = "begin_time"),
            @Result(property = "ownerId", column = "owner_id"),
            @Result(property = "version", column = "version"),
            @Result(property = "period", column = "period"),
            @Result(property = "dateValue", column = "date_value"),
            @Result(property = "genType", column = "gen_type"),
            @Result(property = "createTime", column = "create_time"),

            @Result(property = "dependencies", column = "schedule_code", javaType = List.class, many = @Many(select = "getDependenciesByScheduleCode"))

    })
    List<CaesarScheduleConfigInfoBo> getAllCaesarSystemSchedulerConfigs();

    @Select("select * from caesar_schedule_dependency where schedule_code = #{scheduleCode}")
    CaesarScheduleDependency getDependenciesByScheduleCode(String scheduleCode);
}

