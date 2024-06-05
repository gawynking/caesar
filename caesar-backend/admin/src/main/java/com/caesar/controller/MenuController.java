package com.caesar.controller;

import com.caesar.entity.CaesarMenu;
import com.caesar.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    MenuService menuService;


    @PostMapping("/addFolder")
    private boolean addFolder(@RequestBody CaesarMenu menu){
        return menuService.addFolder(menu);
    }


    @GetMapping("/deleteFolder")
    private boolean deleteFolder(Integer id){
        return menuService.deleteFolder(id);
    }


}
