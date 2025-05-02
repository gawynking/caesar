package com.caesar.controller;


import com.caesar.entity.CaesarTeamGroup;
import com.caesar.entity.CaesarUser;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarUserVo;
import com.caesar.entity.vo.request.JwtRequest;
import com.caesar.entity.vo.response.JwtResponse;
import com.caesar.model.JsonResponse;
import com.caesar.service.LoginService;
import com.caesar.service.TeamGroupService;
import com.caesar.service.UserManagerService;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    LoginService loginService;

    @Autowired
    UserManagerService userService;

    @Autowired
    TeamGroupService teamGroupService;



    @PostMapping("/login")
    public JsonResponse<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {

        if(StringUtils.isEmpty(jwtRequest.getUsername()) || StringUtils.isEmpty(jwtRequest.getPassword())){
            logger.info("用户名或密码为空,请检查后重新登录!");
            return JsonResponse.fail("用户名或密码为空,请检查后重新登录!");
        }

        CaesarUser user = loginService.findByUsername(jwtRequest.getUsername());

        if(null != user && user.getPassword().equals(jwtRequest.getPassword())){
            return JsonResponse.success(new JwtResponse(UUID.randomUUID().toString().replaceAll("-","")));
        }else {
            logger.info("用户名或密码填写错误,请检查后重新登录!");
            return JsonResponse.fail("用户名或密码填写错误,请检查后重新登录!");
        }

    }

    @PostMapping("/logout")
    public JsonResponse<String> logout() {
        return JsonResponse.success("用户注销成功!");
    }

    @PostMapping("/register")
    public JsonResponse<String> register(@RequestBody CaesarUserVo userVo) {

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


    @GetMapping("getTeamList")
    public JsonResponse<List<CaesarTeamGroup>> getTeamList(){
        try {
            List<CaesarTeamGroup> list = teamGroupService.list();
            return JsonResponse.success(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("获取用户组信息失败!");
        return JsonResponse.fail("获取用户组信息失败!");
    }

}
