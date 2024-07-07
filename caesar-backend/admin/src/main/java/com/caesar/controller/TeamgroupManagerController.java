package com.caesar.controller;

import com.caesar.entity.CaesarTeamGroup;
import com.caesar.model.JsonResponse;
import com.caesar.service.TeamGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamgroupManagerController {

    @Autowired
    TeamGroupService teamGroupService;



    @GetMapping("getTeamList")
    public JsonResponse<List<CaesarTeamGroup>> getTeamList(){

        List<CaesarTeamGroup> list = teamGroupService.list();

        return JsonResponse.success(list);

    }



    @PostMapping("/addTeamGroup")
    public JsonResponse<Boolean> addTeamGroup(@RequestBody CaesarTeamGroup teamGroup){
        return JsonResponse.success(teamGroupService.addTeamGroup(teamGroup));
    }



    @GetMapping("/deleteTeamGroup")
    public JsonResponse<Boolean> deleteTeamGroup(@RequestParam int id){
        return JsonResponse.success(teamGroupService.deleteTeamGroup(id));
    }



}