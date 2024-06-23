package com.caesar.controller;

import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.vo.request.AddTaskVo;
import com.caesar.model.JsonResponse;
import com.caesar.model.MenuModel;
import com.caesar.service.MenuService;
import com.caesar.service.TaskService;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.service.UserService;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    MenuService menuService;

    @Autowired
    UserService userService;



    @GetMapping("/listTask")
    public JsonResponse<List<MenuModel.MenuNode>> listTask(@RequestParam String partten){
        List<MenuModel> caesarMenus = new ArrayList<>();
        caesarMenus.addAll(menuService.listByAside());
        caesarMenus.addAll(taskService.listTask(partten));
        List<MenuModel.MenuNode> menuNodes = MenuModel.convert(caesarMenus);
        return JsonResponse.success(menuNodes);
    }


    @PostMapping("/addTask")
    public JsonResponse<Boolean> addTask(@RequestBody AddTaskVo addTaskVo){

        Integer menuId = menuService.getMenuIdFromMenuIndex(addTaskVo.getMenuIndex());
        Integer createdUser = userService.getUserIdFromUserName(addTaskVo.getCreatedUserName());
        Integer updatedUser = userService.getUserIdFromUserName(addTaskVo.getUpdatedUserName());

        CaesarTaskDto caesarTaskDto = BeanConverterTools.convert(addTaskVo, CaesarTaskDto.class);
        caesarTaskDto.setMenuId(menuId);
        caesarTaskDto.setCreatedUser(createdUser);
        caesarTaskDto.setUpdatedUser(updatedUser);

        return JsonResponse.success(taskService.addTask(caesarTaskDto));
    }

    @GetMapping("/deleteTask")
    public JsonResponse<Boolean> deleteTask(@RequestParam(value = "taskName",required = false) String taskName){
        System.out.println("-------> " + taskName);
//        return JsonResponse.success(taskService.deleteTaskFromTaskName(taskName));
        return JsonResponse.success(taskService.markDeleteTaskFromTaskName(taskName));
    }


    @GetMapping("/getTaskInfo")
    public JsonResponse<List<CaesarTaskVo>> getTaskInfo(@RequestParam String taskName){
        List<CaesarTaskVo> caesarTaskVos = taskService.getTaskInfo(taskName);
        return JsonResponse.success(caesarTaskVos);
    }

    @GetMapping("/getCurrentTaskInfo")
    public JsonResponse<CaesarTaskVo> getCurrentTaskInfo(@RequestParam String taskName){
        CaesarTaskVo caesarTaskVo = taskService.getCurrentTaskInfo(taskName);
        return JsonResponse.success(caesarTaskVo);
    }


}
