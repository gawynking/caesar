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
        try {
            List<CaesarUserVo> list = userService.getUserList();
            List<CaesarUserGroupVo> allGroups = userGroupService.getAllGroups();
            for (CaesarUserVo userVo : list) {
                List<CaesarUserGroupVo> userGroups = new ArrayList<>();
                int userId = userVo.getId();
                for (CaesarUserGroupVo userGroupVo : allGroups) {
                    if (userId == userGroupVo.getUserId()) {
                        userGroups.add(userGroupVo);
                    }
                }
                userVo.setGroups(userGroups);
            }

            return JsonResponse.success(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取用户列表失败");
    }

    @GetMapping("/activatedUser")
    public JsonResponse<String> activatedUser(@RequestParam int id){
        try {
            Boolean flag = userService.activatedUser(id);
            if(flag) {
                return JsonResponse.success("激活用户成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("激活用户失败");
    }

    @GetMapping("/deleteUser")
    public JsonResponse<String> delete(@RequestParam int id){
        try {
            boolean flag1 = userService.delete(id);
            boolean flag2 = userGroupService.deleteUser(id);
            if (flag1 && flag2) {
                return JsonResponse.success("添加用户成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除用户失败");
    }


    @PostMapping("/addUser")
    public JsonResponse<String> addUser(@RequestBody CaesarUserVo user){
        try {
            CaesarUser caesarUser = BeanConverterTools.convert(user, CaesarUser.class);
            userService.addUser(caesarUser);
            CaesarUserGroup userGroup = new CaesarUserGroup();
            userGroup.setUserId(userService.getUserIdFromUserName(user.getUsername()));
            userGroup.setGroupId(user.getTeamGroup());
            userGroupService.save(userGroup);
            return JsonResponse.success("添加用户成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("添加用户失败");
    }

}