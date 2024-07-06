package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarMenu;
import com.caesar.model.MenuModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<CaesarMenu> {

    @Insert("insert into caesar_menu(\n" +
            "\tparent_id,\n" +
            "\tmenu_index,\n" +
            "\tmenu_name,\n" +
            "\tlocation,\n" +
            "\tnode_type,\n" +
            "\tmenu_type\n" +
            ")\n" +
            "values(\n" +
            "\t#{parentId}, \n" +
            "\t#{menuIndex}, \n" +
            "\t#{menuName},\n" +
            "\t#{location}, \n" +
            "\t#{nodeType}, \n" +
            "\t#{menuType} \n" +
            ")")
    boolean addFolder(CaesarMenu menu);

    @Select("select * from caesar_menu where id = #{id}")
    CaesarMenu findById(Integer id);

    @Delete("delete from caesar_menu where id = #{id}")
    boolean deleteFolder(Integer id);

    @Select("select sum(1) as subtask_num from caesar_menu where parent_id = #{parentId}")
    Integer findSubtaskNumber(int parentId);

    @Select("select \n" +
            "\tt2.menu_index as parent_index,\n" +
            "\tt1.menu_index as menu_index,\n" +
            "\tt1.menu_name  as menu_name,\n" +
            "\tfalse         as is_leaf\n" +
            "from caesar_menu t1 \n" +
            "join caesar_menu t2 on t1.parent_id = t2.id \n" +
            "where t1.location = 2")
    List<MenuModel> listMenuForAside();

    @Select("select id as menuId\n" +
            "from caesar_menu \n" +
            "where menu_index = #{menuIndex}")
    Integer getMenuIdFromMenuIndex(String menuIndex);

    @Update("update caesar_menu set menu_name = #{menuName} where menu_index = #{menuIndex}")
    boolean renameFolder(CaesarMenu caesarMenu);

    @Select("select t1.id as id\n" +
            "from caesar_menu t1 \n" +
            "left join caesar_menu t2 on t1.parent_id = t2.id  \n" +
            "where t1.location = 2 \n" +
            "  and t1.node_type = 1 \n" +
            "  and t2.node_type = 1")
    List<Integer> findAllowedSubtaskNodes();

}
