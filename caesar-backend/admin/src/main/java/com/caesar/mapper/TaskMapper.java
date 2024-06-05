package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTask;
import com.caesar.model.MenuModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<CaesarTask> {

    @Select("select \n" +
            "\tt2.menu_index as parent_index,\n" +
            "\tt1.task_name  as menu_index,\n" +
            "\tt1.task_name  as menu_name,\n" +
            "\ttrue          as is_leaf\n" +
            "from caesar_task t1 \n" +
            "join caesar_menu t2 on t1.menu_id = t2.id ")
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
            "where t2.username like #{partten1}\n" +
            "  and t3.username like #{partten1}\n" +
            "  and t1.task_name like #{partten2}")
    List<MenuModel> listLikeByOwnerAndTaskNameToMenu(String partten1, String partten2);

    @Select("select id,menu_id,task_type,task_name,exec_engine,version,created_user,updated_user,group_id,task_script,create_time,update_time from caesar_task where task_name = #{taskName}")
    List<CaesarTask> findByName(String taskName);

    @Insert("insert into caesar_task(\n" +
            "\tmenu_id,\n" +
            "\ttask_type,\n" +
            "\ttask_name,\n" +
            "\texec_engine,\n" +
            "\tversion,\n" +
            "\tcreated_user,\n" +
            "\tupdated_user,\n" +
            "\tgroup_id,\n" +
            "\ttask_script\n" +
            ")values(\n" +
            "\t#{menu_id},\n" +
            "\t#{task_type},\n" +
            "\t#{task_name},\n" +
            "\t#{exec_engine},\n" +
            "\t#{version},\n" +
            "\t#{created_user},\n" +
            "\t#{updated_user},\n" +
            "\t#{group_id},\n" +
            "\t#{task_script}\n" +
            ")")
    boolean addTask(CaesarTask task);
}
