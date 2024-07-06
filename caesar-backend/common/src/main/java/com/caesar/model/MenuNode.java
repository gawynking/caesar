package com.caesar.model;


import java.util.List;

public interface MenuNode {

    List<MenuModel.MenuNodeImpl> getChildren();

    void addChild(MenuModel.MenuNodeImpl child);

}
