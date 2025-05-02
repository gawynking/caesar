package com.caesar.controller;

import com.caesar.entity.dto.CaesarMenuDto;
import com.caesar.entity.vo.CaesarMenuVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.MenuManagerService;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;


@RestController
@RequestMapping("/menu")
public class MenuManagerController {

    private static final Logger logger = Logger.getLogger(MenuManagerController.class.getName());

    @Autowired
    MenuManagerService menuManagerService;


    @PostMapping("/addFolder")
    private JsonResponse<String> addFolder(@RequestBody CaesarMenuVo menu){
        try {
            CaesarMenuDto caesarMenuDto = BeanConverterTools.convert(menu, CaesarMenuDto.class);
            boolean flag = menuManagerService.addFolder(caesarMenuDto);
            if(flag){
                return JsonResponse.success("添加目录成功");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("添加目录失败");
    }


    @GetMapping("/deleteFolder")
    private JsonResponse<String> deleteFolder(@RequestParam Integer id){
        try {
            if(menuManagerService.deleteFolder(id)) {
                return JsonResponse.success("删除目录成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除目录失败");
    }


    @PostMapping("/renameFolder")
    private JsonResponse<String> renameFolder(@RequestBody CaesarMenuVo menu){
        try {
            CaesarMenuDto caesarMenuDto = BeanConverterTools.convert(menu, CaesarMenuDto.class);
            if(menuManagerService.renameFolder(caesarMenuDto)) {
                return JsonResponse.success("重命名目录成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("重命名目录失败");
    }

}
