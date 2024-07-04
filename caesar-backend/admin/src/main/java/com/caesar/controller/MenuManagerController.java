package com.caesar.controller;

import com.caesar.entity.dto.CaesarMenuDto;
import com.caesar.model.JsonResponse;
import com.caesar.service.MenuService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.entity.vo.CaesarMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/menu")
public class MenuManagerController {

    @Autowired
    MenuService menuService;


    @PostMapping("/addFolder")
    private JsonResponse<Boolean> addFolder(@RequestBody CaesarMenuVo menu){
        CaesarMenuDto caesarMenuDto = BeanConverterTools.convert(menu, CaesarMenuDto.class);
        return JsonResponse.success(menuService.addFolder(caesarMenuDto));
    }


    @GetMapping("/deleteFolder")
    private JsonResponse<Boolean> deleteFolder(@RequestParam Integer id){
        return JsonResponse.success(menuService.deleteFolder(id));
    }


    @PostMapping("/renameFolder")
    private JsonResponse<Boolean> renameFolder(@RequestBody CaesarMenuVo menu){
        CaesarMenuDto caesarMenuDto = BeanConverterTools.convert(menu, CaesarMenuDto.class);
        return JsonResponse.success(menuService.renameFolder(caesarMenuDto));
    }

}
