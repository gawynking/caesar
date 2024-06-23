package com.caesar.controller;

import com.caesar.entity.CaesarMenu;
import com.caesar.entity.dto.CaesarMenuDto;
import com.caesar.service.MenuService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.entity.vo.CaesarMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    MenuService menuService;


    @PostMapping("/addFolder")
    private boolean addFolder(@RequestBody CaesarMenuVo menu){
        CaesarMenuDto caesarMenuDto = BeanConverterTools.convert(menu, CaesarMenuDto.class);
        return menuService.addFolder(caesarMenuDto);
    }


    @GetMapping("/deleteFolder")
    private boolean deleteFolder(@RequestParam Integer id){
        return menuService.deleteFolder(id);
    }


}
