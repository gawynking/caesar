package com.caesar.controller;


import com.caesar.entity.CaesarTeamGroup;
import com.caesar.entity.CaesarUser;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarTeamGroupVo;
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


@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    LoginService loginService;

    @Autowired
    UserManagerService userService;

    @Autowired
    TeamGroupService teamGroupService;

    @PostMapping("/login")
    public JsonResponse<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {

        if(StringUtils.isEmpty(jwtRequest.getUsername()) || StringUtils.isEmpty(jwtRequest.getPassword())){
            return JsonResponse.fail("用户名或密码错误");
        }

        CaesarUser user = loginService.findByUsername(jwtRequest.getUsername());

        if(null != user && user.getPassword().equals(jwtRequest.getPassword())){
            return JsonResponse.success(new JwtResponse("登录成功"));
        }

        return JsonResponse.fail("用户名或密码错误");
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logged out successfully";
    }

    @PostMapping("/register")
    public JsonResponse<String> register(@RequestBody CaesarUserVo userVo) {

        if(StringUtils.isEmpty(userVo.getUsername()) || StringUtils.isEmpty(userVo.getPassword())){
            return JsonResponse.fail("用户名或密码错误");
        }

        CaesarUser caesarUser = BeanConverterTools.convert(userVo, CaesarUser.class);
        boolean res1 = userService.save(caesarUser);

        CaesarUserGroup userGroup = new CaesarUserGroup();
        userGroup.setUserId(userService.getUserIdFromUserName(userVo.getUsername()));
        userGroup.setGroupId(userVo.getTeamGroup());
        boolean res2 = userService.addUserGroup(userGroup);

        boolean res = res1 && res2;
        return JsonResponse.success(res?"注册用户成功，请登录":"用户注册失败");

    }


    @GetMapping("getTeamList")
    public JsonResponse<List<CaesarTeamGroup>> getTeamList(){
        List<CaesarTeamGroup> list = teamGroupService.list();
        return JsonResponse.success(list);

    }

}
