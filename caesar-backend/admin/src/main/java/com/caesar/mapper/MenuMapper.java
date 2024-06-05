package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarMenu;
import com.caesar.model.MenuModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<CaesarMenu> {

    @Select("select id from caesar_menu where location = 2 and node_type = 1 and parent_id in (5)")
    List<Integer> allowedSubtask();

    @Insert("insert into caesar_menu(" +
            "location," +
            "node_type," +
            "parent_id," +
            "menu_type," +
            "menu_index," +
            "menu_name" +
            ")" +
            "values(" +
            "#{location}, " +
            "#{nodeType}, " +
            "#{parentId}, " +
            "#{menuType}, " +
            "#{menuIndex}, " +
            "#{menuName}" +
            ");")
    boolean addFolder(CaesarMenu menu);

    @Select("select id,location,node_type,parent_id,menu_type,menu_index,menu_name,create_time,update_time from caesar_menu where id = #{id}")
    CaesarMenu findById(Integer id);

    @Delete("delete from caesar_menu where id = #{id}")
    boolean deleteFolder(Integer id);

    @Select("select sum(1) as subtask_num from caesar_menu where parent_id = #{id}")
    Integer findSubtask(int id);

    @Select("select \n" +
            "\tt2.menu_index as parent_index,\n" +
            "\tt1.menu_index as menu_index,\n" +
            "\tt1.menu_name  as menu_name,\n" +
            "\tfalse         as is_leaf\n" +
            "from caesar_menu t1 \n" +
            "join caesar_menu t2 on t1.parent_id = t2.id \n" +
            "where t1.location = 2 \n" +
            "and t1.parent_id in (1,5)")
    List<MenuModel> listByAside();

}
