package com.caesar.controller;

import com.caesar.entity.dto.CaesarMenuDto;
import com.caesar.entity.vo.CaesarMenuVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.MenuManagerService;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/menu")
public class MenuManagerController {

    @Autowired
    MenuManagerService menuManagerService;


    @PostMapping("/addFolder")
    private JsonResponse<Boolean> addFolder(@RequestBody CaesarMenuVo menu){
        CaesarMenuDto caesarMenuDto = BeanConverterTools.convert(menu, CaesarMenuDto.class);
        return JsonResponse.success(menuManagerService.addFolder(caesarMenuDto));
    }


    @GetMapping("/deleteFolder")
    private JsonResponse<Boolean> deleteFolder(@RequestParam Integer id){
        return JsonResponse.success(menuManagerService.deleteFolder(id));
    }


    @PostMapping("/renameFolder")
    private JsonResponse<Boolean> renameFolder(@RequestBody CaesarMenuVo menu){
        CaesarMenuDto caesarMenuDto = BeanConverterTools.convert(menu, CaesarMenuDto.class);
        return JsonResponse.success(menuManagerService.renameFolder(caesarMenuDto));
    }

}
