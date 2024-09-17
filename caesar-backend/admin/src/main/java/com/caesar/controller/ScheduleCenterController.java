package com.caesar.controller;

import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.entity.vo.response.ScheduleBaseInfoVo;
import com.caesar.entity.vo.response.ScheduleInfoVo;
import com.caesar.entity.vo.response.TaskDependency;
import com.caesar.exception.SqlParseException;
import com.caesar.model.JsonResponse;
import com.caesar.service.ScheduleCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
public class ScheduleCenterController {


    @Autowired
    ScheduleCenterService scheduleCenterService;


    @GetMapping("/getScheduleBaseInfo")
    public JsonResponse<ScheduleBaseInfoVo> getScheduleBaseInfo(){

        ScheduleBaseInfoVo scheduleBaseInfoVo = scheduleCenterService.getScheduleBaseInfo();
        return JsonResponse.success(scheduleBaseInfoVo);

    }


    @GetMapping("/getTaskSchedules")
    public JsonResponse<List<ScheduleInfoVo>> getTaskSchedules(@RequestParam String taskName){
        return JsonResponse.success(scheduleCenterService.getTaskSchedules(taskName));
    }


    @GetMapping("/getTaskSchedule")
    public JsonResponse<ScheduleInfoVo> getTaskSchedule(@RequestParam String scheduleName){
        return JsonResponse.success(scheduleCenterService.getTaskSchedule(scheduleName));
    }


    @PostMapping("/genTaskSchedule")
    public JsonResponse<Boolean> genTaskSchedule(@RequestBody GeneralScheduleInfoVo scheduleInfo){
        System.out.println("请求参数 = " + scheduleInfo);
        return JsonResponse.success(scheduleCenterService.genTaskSchedule(scheduleInfo));
    }


    @PostMapping("/updateTaskSchedule")
    public JsonResponse<Boolean> updateTaskSchedule(@RequestBody GeneralScheduleInfoVo scheduleInfo){
        return JsonResponse.success(scheduleCenterService.updateTaskSchedule(scheduleInfo));
    }


    @PostMapping("/deleteTaskSchedule")
    public JsonResponse<Boolean> deleteTaskSchedule(@RequestBody String scheduleName){
        return JsonResponse.success(scheduleCenterService.deleteTaskSchedule(scheduleName));
    }


    @GetMapping("/getTaskDependencies")
    public JsonResponse<List<TaskDependency>> getTaskDependencies(
            @RequestParam String taskName,
            @RequestParam Integer version,
            @RequestParam String period){
        try {
            return JsonResponse.success(scheduleCenterService.getTaskDependencies(taskName,version,period));
        } catch (SqlParseException e) {
            e.printStackTrace();
            return JsonResponse.fail();
        }
    }


}
