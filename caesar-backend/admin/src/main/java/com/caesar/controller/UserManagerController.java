package com.caesar.controller;

import com.caesar.entity.CaesarUser;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserGroupVo;
import com.caesar.entity.vo.CaesarUserVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.UserGroupService;
import com.caesar.service.UserManagerService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/user")
public class UserManagerController {

    private static final Logger logger = Logger.getLogger(UserManagerController.class.getName());

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
        logger.info("获取用户列表失败");
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
                return JsonResponse.success("删除用户成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除用户失败");
    }


    @PostMapping("/addUser")
    public JsonResponse<String> addUser(@RequestBody CaesarUserVo userVo){

        if(StringUtils.isEmpty(userVo.getUsername()) || StringUtils.isEmpty(userVo.getPassword())){
            logger.info("用户名或密码为空,请正确填写用户名和密码后重试!");
            return JsonResponse.fail("用户名或密码为空,请正确填写用户名和密码后重试!");
        }

        Integer registerUserId = userService.getUserIdFromUserName(userVo.getUsername());
        if(null != registerUserId && registerUserId > 0){
            logger.info("用户名已存在,请正确填写用户名和密码后重试!");
            return JsonResponse.fail("用户名已存在,请正确填写用户名和密码后重试!");
        }

        if(0 == userVo.getTeamGroup()){
            logger.info("注册用户必须属于一个团队,请正确选择团队组后重试!");
            return JsonResponse.fail("注册用户必须属于一个团队,请正确选择团队组后重试!");
        }

        List<CaesarUser> userList = userService.list();
        for(CaesarUser user :userList){
            if(user.getEmail().equals(userVo.getEmail()) || user.getPhone().equals(userVo.getPhone())){
                logger.info("邮箱或手机号已经存在,请使用新账号重试!");
                return JsonResponse.fail("邮箱或手机号已经存在,请使用新账号重试!");
            }
        }

        CaesarUser caesarUser = BeanConverterTools.convert(userVo, CaesarUser.class);
        boolean res1 = userService.save(caesarUser);

        CaesarUserGroup userGroup = new CaesarUserGroup();
        userGroup.setUserId(userService.getUserIdFromUserName(userVo.getUsername()));
        userGroup.setGroupId(userVo.getTeamGroup());
        boolean res2 = userService.addUserGroup(userGroup);

        boolean res = res1 && res2;
        if(res){
            return JsonResponse.success("注册用户成功,请登录!");
        }else {
            logger.info("用户注册失败,请确认信息重试!");
            return JsonResponse.fail("用户注册失败,请确认信息重试!");
        }

    }

    @GetMapping("/getUserIdFromUsername")
    public JsonResponse<Integer> getUserIdFromUsername(@RequestParam String userName){
        try {
            Integer userId = userService.getUserIdFromUserName(userName);
            return JsonResponse.success(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("Username转userId失败");
        return JsonResponse.fail("Username转userId失败");
    }

}