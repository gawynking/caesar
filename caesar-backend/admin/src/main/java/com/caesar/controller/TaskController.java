package com.caesar.controller;

import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.model.JsonResponse;
import com.caesar.model.MenuModel;
import com.caesar.service.MenuService;
import com.caesar.service.TaskService;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    MenuService menuService;


    @GetMapping("/listTask")
    public JsonResponse<List<MenuModel.MenuNode>> listTask(@RequestParam String partten){
        List<MenuModel> caesarMenus = new ArrayList<>();
        caesarMenus.addAll(menuService.listByAside());
        caesarMenus.addAll(taskService.listTask(partten));
        List<MenuModel.MenuNode> menuNodes = MenuModel.convert(caesarMenus);
        return JsonResponse.success(menuNodes);
    }



    @PostMapping("/addTask")
    public boolean addTask(@RequestBody CaesarTaskVo task){
        CaesarTaskDto caesarTaskDto = BeanConverterTools.convert(task, CaesarTaskDto.class);
        return taskService.addTask(caesarTaskDto);
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
