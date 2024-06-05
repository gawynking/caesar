package com.caesar.controller;

import com.caesar.entity.CaesarTask;
import com.caesar.model.JsonResponse;
import com.caesar.model.MenuModel;
import com.caesar.service.MenuService;
import com.caesar.service.TaskService;
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
    public JsonResponse<List<MenuModel.MenuNode>> listTask(String partten){
        List<MenuModel> caesarMenus = new ArrayList<>();
        caesarMenus.addAll(menuService.listByAside());
        caesarMenus.addAll(taskService.listTask(partten));
        List<MenuModel.MenuNode> menuNodes = MenuModel.convert(caesarMenus);
        return JsonResponse.success(menuNodes);
    }


    @PostMapping("/addTask")
    public boolean addTask(@RequestBody CaesarTask task){
        return taskService.addTask(task);
    }

}
