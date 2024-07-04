package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTask;
import com.caesar.model.MenuModel;
import com.caesar.entity.vo.CaesarTaskVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<CaesarTask> {

    @Select("select \n" +
            "\tt3.menu_index as parent_index,\n" +
            "\tt1.task_name  as menu_index,\n" +
            "\tt1.task_name  as menu_name,\n" +
            "\ttrue          as is_leaf\n" +
            "from caesar_task t1 \n" +
            "join (\n" +
            "\tselect max(id) as id \n" +
            "\tfrom caesar_task\n" +
            "\tgroup by task_name \n" +
            ") t2 on t1.id = t2.id \n" +
            "join caesar_menu t3 on t1.menu_id = t3.id\n" +
            "where t1.is_deleted = 0\n" +
            "order by t1.update_time desc")
    List<MenuModel> listToMenu();

    @Select("select \n" +
            "\tt4.menu_index as parent_index,\n" +
            "\tt1.task_name  as menu_index,\n" +
            "\tt1.task_name  as menu_name,\n" +
            "\ttrue          as is_leaf\n" +
            "from caesar_task t1 \n" +
            "join caesar_user t2 on t1.created_user = t2.id \n" +
            "join caesar_user t3 on t1.updated_user = t3.id \n" +
            "join caesar_menu t4 on t1.menu_id = t4.id \n" +
            "join (\n" +
            "\tselect max(id) as id \n" +
            "\tfrom caesar_task\n" +
            "\tgroup by task_name \n" +
            ") t5 on t1.id = t5.id \n" +
            "where t2.username like #{partten1}\n" +
            "  and t3.username like #{partten1}\n" +
            "  and t1.task_name like #{partten2}\n" +
            "  and t1.is_deleted = 0\n" +
            "order by t1.update_time desc")
    List<MenuModel> listLikeByOwnerAndTaskNameToMenu(String partten1, String partten2);

    @Select("select * from caesar_task where task_name = #{taskName}")
    List<CaesarTask> findByName(String taskName);

    @Insert("insert into caesar_task(\n" +
            "\tmenu_id,\n" +
            "\ttask_type,\n" +
            "\ttask_name,\n" +
            "\tdatasource_info,\n" +
            "\tengine,\n" +
            "\tversion,\n" +
            "\tgroup_id,\n" +
            "\tis_released,\n" +
            "\tis_online,\n" +
            "\ttask_script,\n" +
            "\tcreated_user,\n" +
            "\tupdated_user\n" +
            ")values(\n" +
            "\t#{menuId},\n" +
            "\t#{taskType},\n" +
            "\t#{taskName},\n" +
            "\t#{datasourceInfo},\n" +
            "\t#{engine},\n" +
            "\t#{version},\n" +
            "\t#{groupId},\n" +
            "\t#{isReleased},\n" +
            "\t#{isOnline},\n" +
            "\t#{taskScript},\n" +
            "\t#{createdUser},\n" +
            "\t#{updatedUser}\n" +
            ")")
    boolean addTask(CaesarTask task);

    @Select("select \n" +
            "\tt1.menu_id         as menu_id,\n" +
            "\tt1.task_type       as task_type,\n" +
            "\tt1.task_name       as task_name,\n" +
            "\tt1.datasource_info as datasource_info,\n" +
            "\tt1.engine          as engine,\n" +
            "\tt1.version         as version,\n" +
            "\tt1.task_script     as task_script\n" +
            "from caesar_task t1 \n" +
            "join (\n" +
            "\tselect max(id) as id \n" +
            "\tfrom caesar_task\n" +
            "\twhere task_name = #{taskName}\n" +
            ") t2 on t1.id = t2.id \n" +
            "where t1.task_name = #{taskName}\n" +
            "  and t1.is_deleted = 0\n" +
            ";")
    CaesarTaskVo getCurrentTaskInfo(String taskName);

    @Select("select \n" +
            "\tt1.menu_id         as menu_id,\n" +
            "\tt1.task_type       as task_type,\n" +
            "\tt1.task_name       as task_name,\n" +
            "\tt1.datasource_info as datasource_info,\n" +
            "\tt1.engine          as engine,\n" +
            "\tt1.version         as version,\n" +
            "\tt1.task_script     as task_script\n" +
            "from caesar_task t1 \n" +
            "where t1.task_name = #{taskName}\n" +
            "  and t1.is_deleted = 0\n" +
            ";")
    List<CaesarTaskVo> getTaskInfo(String taskName);

    @Select("select max(version) as version from caesar_task")
    Integer getVersion();

    @Delete("delete from caesar_task where task_name = #{taskName}")
    Boolean deleteTaskFromTaskName(String taskName);

    @Update("update caesar_task set is_deleted = 1 where task_name = #{taskName}")
    Boolean markDeleteTaskFromTaskName(String taskName);

    @Update("update caesar_task set is_deleted = 0 where task_name = #{taskName}")
    boolean martDeleteToOnline(String taskName);

}
