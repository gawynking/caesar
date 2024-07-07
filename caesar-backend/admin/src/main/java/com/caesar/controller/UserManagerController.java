package com.caesar.controller;

import com.caesar.entity.CaesarUser;
import com.caesar.model.JsonResponse;
import com.caesar.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserManagerController {

    @Autowired
    UserManagerService userService;


    @GetMapping("getUserList")
    public JsonResponse<List<CaesarUser>> getUserList(){

        List<CaesarUser> list = userService.list();

        return JsonResponse.success(list);

    }

    @GetMapping("/activateUser")
    public JsonResponse<Boolean> activateUser(@RequestParam int id){
        return JsonResponse.success(userService.activatedUser(id));
    }

    @GetMapping("/deleteUser")
    public JsonResponse<Boolean> delete(@RequestParam int id){
        return JsonResponse.success(userService.delete(id));
    }

    @PostMapping("/addUser")
    public JsonResponse<Boolean> addUser(@RequestBody CaesarUser user){
        return JsonResponse.success(userService.addUser(user));
    }

}