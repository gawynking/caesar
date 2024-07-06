package com.caesar.controller;

import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.vo.request.AddTaskVo;
import com.caesar.model.JsonResponse;
import com.caesar.model.MenuModel;
import com.caesar.model.MenuNode;
import com.caesar.service.MenuManagerService;
import com.caesar.service.DevelopCenterService;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.service.UserManagerService;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/develop")
public class DevelopCenterController {

    @Autowired
    DevelopCenterService developCenterService;

    @Autowired
    MenuManagerService menuManagerService;

    @Autowired
    UserManagerService userManagerService;



    @GetMapping("/listTask")
    public JsonResponse<List<MenuNode>> listTask(@RequestParam String partten){
        List<MenuModel> caesarMenus = new ArrayList<>();
        caesarMenus.addAll(menuManagerService.listMenuForAside());
        caesarMenus.addAll(developCenterService.listTaskToMenu(partten));
        List<MenuNode> menuNodes = MenuModel.convert(caesarMenus);
        return JsonResponse.success(menuNodes);
    }


    @PostMapping("/addTask")
    public JsonResponse<Boolean> addTask(@RequestBody AddTaskVo addTaskVo){

        Integer menuId = menuManagerService.getMenuIdFromMenuIndex(addTaskVo.getMenuIndex());
        Integer createdUser = userManagerService.getUserIdFromUserName(addTaskVo.getCreatedUserName());
        Integer updatedUser = userManagerService.getUserIdFromUserName(addTaskVo.getUpdatedUserName());

        CaesarTaskDto caesarTaskDto = BeanConverterTools.convert(addTaskVo, CaesarTaskDto.class);
        caesarTaskDto.setMenuId(menuId);
        caesarTaskDto.setCreatedUser(createdUser);
        caesarTaskDto.setUpdatedUser(updatedUser);

        return JsonResponse.success(developCenterService.addTask(caesarTaskDto));
    }

    @GetMapping("/deleteTask")
    public JsonResponse<Boolean> deleteTask(@RequestParam(value = "taskName",required = false) String taskName){
//        return JsonResponse.success(taskService.deleteTaskFromTaskName(taskName));
        return JsonResponse.success(developCenterService.markDeletedTaskFromTaskName(taskName));
    }


    @GetMapping("/getTaskInfos")
    public JsonResponse<List<CaesarTaskVo>> getTaskInfos(@RequestParam String taskName){
        List<CaesarTaskVo> caesarTaskVos = developCenterService.getTaskInfos(taskName);
        return JsonResponse.success(caesarTaskVos);
    }

    @GetMapping("/getCurrentTaskInfo")
    public JsonResponse<CaesarTaskVo> getCurrentTaskInfo(@RequestParam String taskName){
        CaesarTaskVo caesarTaskVo = developCenterService.getCurrentTaskInfo(taskName);
        return JsonResponse.success(caesarTaskVo);
    }


}
