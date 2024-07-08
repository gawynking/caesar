package com.caesar.controller;

import com.caesar.entity.CaesarUser;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserGroupVo;
import com.caesar.entity.vo.CaesarUserVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.UserGroupService;
import com.caesar.service.UserManagerService;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserManagerController {

    @Autowired
    UserManagerService userService;

    @Autowired
    UserGroupService userGroupService;


    @GetMapping("getUserList")
    public JsonResponse<List<CaesarUserVo>> getUserList(){

        List<CaesarUserVo> list = userService.getUserList();
        List<CaesarUserGroupVo> allGroups = userGroupService.getAllGroups();
        for(CaesarUserVo userVo:list){
            List<CaesarUserGroupVo> userGroups = new ArrayList<>();
            int userId = userVo.getId();
            for (CaesarUserGroupVo userGroupVo:allGroups){
                if(userId == userGroupVo.getUserId()){
                    userGroups.add(userGroupVo);
                }
            }
            userVo.setGroups(userGroups);
        }

        return JsonResponse.success(list);

    }

    @GetMapping("/activatedUser")
    public JsonResponse<Boolean> activatedUser(@RequestParam int id){
        return JsonResponse.success(userService.activatedUser(id));
    }

    @GetMapping("/deleteUser")
    public JsonResponse<Boolean> delete(@RequestParam int id){
        userService.delete(id);
        userGroupService.deleteUser(id);
        return JsonResponse.success();
    }

    @PostMapping("/addUser")
    public JsonResponse<Boolean> addUser(@RequestBody CaesarUserVo user){
        CaesarUser caesarUser = BeanConverterTools.convert(user, CaesarUser.class);
        userService.addUser(caesarUser);
        CaesarUserGroup userGroup = new CaesarUserGroup();
        userGroup.setUserId(userService.getUserIdFromUserName(user.getUsername()));
        userGroup.setGroupId(user.getTeamGroup());
        userGroupService.save(userGroup);
        return JsonResponse.success(true);
    }

}