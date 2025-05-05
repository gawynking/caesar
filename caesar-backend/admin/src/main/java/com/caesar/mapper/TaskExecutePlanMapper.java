package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskExecutePlan;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface TaskExecutePlanMapper extends BaseMapper<CaesarTaskExecutePlan> {

    @Insert("insert into caesar_task_execute_plan(\n" +
            "\tuuid,\n" +
            "\ttask_id,\n" +
            "\ttask_name,\n" +
            "\ttask_version,\n" +
            "\tenvironment,\n" +
            "\tperiod,\n" +
            "\tstart_date,\n" +
            "\tend_date,\n" +
            "\tstatus\n" +
            ")values(\n" +
            "\t#{uuid},\n" +
            "\t#{taskId},\n" +
            "\t#{taskName},\n" +
            "\t#{taskVersion},\n" +
            "\t#{environment},\n" +
            "\t#{period},\n" +
            "\t#{startDate},\n" +
            "\t#{endDate},\n" +
            "\t#{status}\n" +
            ")")
    Boolean save(CaesarTaskExecutePlan taskExecutePlan);

    @Update("update caesar_task_execute_plan set status = #{status} where uuid = #{planUuid}")
    void updatePlanStatus(String planUuid, int status);

}
