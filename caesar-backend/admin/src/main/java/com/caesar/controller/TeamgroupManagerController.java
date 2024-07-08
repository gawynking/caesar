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

        List<CaesarTeamGroupVo> list = teamGroupService.getTeamList();

        return JsonResponse.success(list);

    }



    @PostMapping("/addTeamGroup")
    public JsonResponse<Boolean> addTeamGroup(@RequestBody CaesarTeamGroup teamGroup){
        return JsonResponse.success(teamGroupService.addTeamGroup(teamGroup));
    }



    @GetMapping("/deleteTeamGroup")
    public JsonResponse<Boolean> deleteTeamGroup(@RequestParam int id){
        List<CaesarUserGroup> userGroups = userGroupService.getUserGroupsByGroupId(id);
        if(null != userGroups && userGroups.size()>0){
            return JsonResponse.fail("该组存在用户，不能删除.");
        }
        return JsonResponse.success(teamGroupService.deleteTeamGroup(id));
    }



}