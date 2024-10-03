package com.caesar.controller;

import com.caesar.entity.CaesarTeamGroup;
import com.caesar.entity.CaesarUserGroup;
import com.caesar.entity.vo.CaesarTeamGroupVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.TeamGroupService;
import com.caesar.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamgroupManagerController {

    @Autowired
    TeamGroupService teamGroupService;

    @Autowired
    UserGroupService userGroupService;


    @GetMapping("getTeamList")
    public JsonResponse<List<CaesarTeamGroupVo>> getTeamList(){

        try {
            List<CaesarTeamGroupVo> list = teamGroupService.getTeamList();
            return JsonResponse.success(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取用户组信息失败");
    }



    @PostMapping("/addTeamGroup")
    public JsonResponse<String> addTeamGroup(@RequestBody CaesarTeamGroup teamGroup){
        try {
            if(teamGroupService.addTeamGroup(teamGroup)) {
                return JsonResponse.success("添加用户组成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("添加用户组失败");
    }



    @GetMapping("/deleteTeamGroup")
    public JsonResponse<String> deleteTeamGroup(@RequestParam int id){
        try {
            List<CaesarUserGroup> userGroups = userGroupService.getUserGroupsByGroupId(id);
            if (null != userGroups && userGroups.size() > 0) {
                return JsonResponse.fail("该组存在用户，不能删除.");
            }
            boolean flag = teamGroupService.deleteTeamGroup(id);
            if(flag) {
                return JsonResponse.success("删除用户组成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除用户组失败");
    }

}
