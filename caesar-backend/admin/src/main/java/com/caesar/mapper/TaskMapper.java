package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.vo.CaesarTaskParameterVo;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.model.MenuModel;
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
    List<MenuModel> listTaskToMenu();

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
            "where (t2.username like #{pattern} or t3.username like #{pattern} or t1.task_name like #{pattern})\n" +
            "  and t1.is_deleted = 0\n" +
            "order by t1.update_time desc")
    List<MenuModel> listTaskToMenuByOwnerOrTaskname(String pattern);

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
            "where (t2.username like #{pattern1} or t3.username like #{pattern1})\n" +
            "  and t1.task_name like #{pattern2}\n" +
            "  and t1.is_deleted = 0\n" +
            "order by t1.update_time desc")
    List<MenuModel> listTaskToMenuByOwnerAndTaskname(String pattern1, String pattern2);

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
            "\tchecksum,\n" +
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
            "\t#{checksum},\n" +
            "\t#{createdUser},\n" +
            "\t#{updatedUser}\n" +
            ")")
    boolean addTask(CaesarTask task);

    @Select("select \n" +
            "\tt1.menu_id         as menu_id,\n" +
            "\tt1.task_type       as task_type,\n" +
            "\tt1.task_name       as task_name,\n" +
            "\tt1.group_id        as group_id,\n" +
            "\tt1.is_released     as is_released,\n" +
            "\tt1.is_online       as is_online,\n" +
            "\tt1.datasource_info as datasource_info,\n" +
            "\tt1.engine          as engine,\n" +
            "\tt1.version         as version,\n" +
            "\tt1.created_user    as created_user,\n" +
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
            "\tt1.group_id        as group_id,\n" +
            "\tt1.datasource_info as datasource_info,\n" +
            "\tt1.engine          as engine,\n" +
            "\tt1.version         as version,\n" +
            "\tt1.task_script     as task_script\n" +
            "from caesar_task t1 \n" +
            "where t1.task_name = #{taskName}\n" +
            "  and t1.is_deleted = 0\n" +
            ";")
    List<CaesarTaskVo> getTaskInfos(String taskName);

    @Select("select max(version) as version from caesar_task")
    Integer getVersion();

    @Delete("delete from caesar_task where task_name = #{taskName}")
    Boolean deleteTaskFromTaskName(String taskName);

    @Update("update caesar_task set is_deleted = 1 where task_name = #{taskName}")
    Boolean markDeletedTaskFromTaskName(String taskName);

    @Update("update caesar_task set is_deleted = 0 where task_name = #{taskName}")
    boolean recoverDeletedTaskFromTaskName(String taskName);

    @Select("select checksum \n" +
            "from caesar_task \n" +
            "where task_name = #{taskName}\n" +
            "  and version = #{version}")
    String getTaskChecksumFromVersion(String taskName, int version);

    @Select("select * \n" +
            "from caesar_task \n" +
            "where task_name = #{taskName}\n" +
            "  and is_deleted = 0 \n" +
            "order by update_time desc ")
    List<CaesarTaskVo> getTaskVersions(String taskName);

    @Select("select param_name,param_desc,expression from caesar_task_parameter")
    List<CaesarTaskParameterVo> getParams();

    @Select("select * from caesar_task where task_name = #{taskName} and version = #{version}")
    CaesarTaskVo getCurrentTaskInfoWithVersion(String taskName, int version);

    @Select("select * from caesar_task where id = #{taskId}")
    CaesarTask getTaskInfoFromId(int taskId);

    @Select("select 1 from caesar_task where id = #{taskId} and is_online = 1")
    Integer checkOnlineById(int taskId);

    @Select("select 1 as is_online\n" +
            "from caesar_task \n" +
            "where id = #{taskId}\n" +
            "  and is_online = 1")
    Boolean checkTaskVersionIsOnline(int taskId);

    @Update("update caesar_task set is_released = 1,is_online = 1 where id = #{taskId}")
    Boolean taskPassReview2Online(int taskId);

    @Select("select * \n" +
            "from caesar_task \n" +
            "where task_name = #{taskName}\n" +
            "  and version < #{version}\n" +
            "order by version desc \n" +
            "limit 1")
    CaesarTask getPreVersionTaskInfo(String taskName, int version);

    @Select("select * \n" +
            "from caesar_task \n" +
            "where task_name = #{taskName}\n" +
            "  and is_online = 1")
    CaesarTask getOnlineTaskInfo(String taskName);

    @Update("update caesar_task set is_online = 0 where id = #{taskId} and is_online = 1")
    Boolean currentVersionTaskOffline(int taskId);

    @Select("select t1.* \n" +
            "from caesar_task t1 \n" +
            "join (\n" +
            "\tselect task_name \n" +
            "\tfrom caesar_task \n" +
            "\twhere id = #{taskId} \n" +
            ") t2 on t1.task_name = t2.task_name \n" +
            "where t1.is_online = 1")
    CaesarTask getTaskOnlineVersionInfoFromReviewTaskId(int taskId);

    @Select("select id from caesar_task where version = #{version}")
    Integer getTaskIdByVersion(Integer version);

    @Select("select * from caesar_task where version = #{version}")
    CaesarTask getTaskInfoFromVersion(Integer version);

    @Select("select max(1) \n" +
            "from caesar_task \n" +
            "where is_released = 1 \n" +
            "  and id = #{taskId}")
    Boolean validateTaskPublish(int taskId);

    @Select("select t1.version as preVersion\n" +
            "from caesar_task t1 \n" +
            "join (\n" +
            "\tselect task_name,version \n" +
            "\tfrom caesar_task\n" +
            "\twhere id = #{taskId}\n" +
            ") t2 on t1.task_name = t2.task_name \n" +
            "where t1.is_released = 1 \n" +
            "  and t1.id = #{taskId}\n" +
            "  and t1.version < t2.version \n" +
            "order by t1.version \n" +
            "limit 1")
    Integer getTaskPreVersionReleased(int taskId);
}
