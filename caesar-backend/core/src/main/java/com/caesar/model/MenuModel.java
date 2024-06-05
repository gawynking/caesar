package com.caesar.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MenuModel {

    String parentIndex;
    String menuIndex;
    String menuName;
    Boolean isLeaf;


    public static List<MenuNode> convert(List<MenuModel> menus) {

        Map<String, MenuNode> nodeMap = new HashMap<>();
        List<MenuNode> roots = new ArrayList<>();

        // Create all nodes and map them by their menu_id
        for (MenuModel menu : menus) {
            MenuNode node = new MenuNode(menu.getMenuIndex(), menu.getMenuName(), menu.getIsLeaf());
            nodeMap.put(menu.menuIndex, node);
        }

        // Link children to their parent nodes
        for (MenuModel menu : menus) {
            MenuNode node = nodeMap.get(menu.menuIndex);
            if ("task".equals(menu.parentIndex)) {
                // Root node
                roots.add(node);
            } else {
                MenuNode parentNode = nodeMap.get(menu.parentIndex);
                if (parentNode != null) {
                    parentNode.addChild(node);
                }
            }
        }

        return roots;
    }


    public static class MenuNode {

        private String menuIndex;
        private String menuName;
        private Boolean isLeaf;
        private List<MenuNode> children;



        public MenuNode(String menuIndex, String menuName,Boolean isLeaf) {
            this.menuIndex = menuIndex;
            this.menuName = menuName;
            this.isLeaf = isLeaf;
            this.children = new ArrayList<>();
        }

        public String getMenuIndex() {
            return this.menuIndex;
        }

        public String getMenuName() {
            return this.menuName;
        }

        public Boolean getIsLeaf() {
            return this.isLeaf;
        }

        public List<MenuNode> getChildren() {
            return this.children;
        }

        public void addChild(MenuNode child) {
            this.children.add(child);
        }

    }

}
