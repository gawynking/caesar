package com.caesar.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.caesar.entity.CaesarScheduleCluster;
import com.caesar.entity.vo.CaesarScheduleClusterVo;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.entity.vo.response.ScheduleBaseInfoVo;
import com.caesar.entity.vo.response.ScheduleInfoVo;
import com.caesar.entity.vo.response.TaskDependency;
import com.caesar.exception.SqlParseException;
import com.caesar.model.JsonResponse;
import com.caesar.service.ScheduleCenterService;
import com.caesar.service.ScheduleClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
public class ScheduleCenterController {


    @Autowired
    ScheduleCenterService scheduleCenterService;

    @Autowired
    ScheduleClusterService scheduleClusterService;



    @GetMapping("/getScheduleClusters")
    public JsonResponse<List<CaesarScheduleCluster>> getScheduleClusters(){
        try {
            List<CaesarScheduleCluster> caesarScheduleClusters = scheduleClusterService.list();
            return JsonResponse.success(caesarScheduleClusters);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取调度集群列表失败");
    }


    @PostMapping("/addScheduleCluster")
    public JsonResponse<String> addScheduleCluster(@RequestBody CaesarScheduleClusterVo scheduleClusterVo){
        try {
            CaesarScheduleCluster caesarScheduleCluster = new CaesarScheduleCluster();
            caesarScheduleCluster.setIpAddr(scheduleClusterVo.getIpAddr());
            caesarScheduleCluster.setScheduleCategory(scheduleClusterVo.getScheduleCategory());
            boolean isSave = scheduleClusterService.save(caesarScheduleCluster);
            if(isSave){
                return JsonResponse.success("添加集群节点成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("添加集群节点失败");
    }


    @PostMapping("/updateScheduleCluster")
    public JsonResponse<String> updateScheduleCluster(@RequestBody CaesarScheduleClusterVo scheduleClusterVo){
        try {
            CaesarScheduleCluster caesarScheduleCluster = scheduleClusterService.getById(scheduleClusterVo.getId());
            caesarScheduleCluster.setIpAddr(scheduleClusterVo.getIpAddr());
            caesarScheduleCluster.setScheduleCategory(scheduleClusterVo.getScheduleCategory());
            Boolean isUpdated = scheduleClusterService.updateById(caesarScheduleCluster);
            if (isUpdated) {
                return JsonResponse.success("更新集群节点成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("更新集群节点失败");
    }


    @GetMapping("/deleteScheduleCluster")
    public JsonResponse<String> deleteScheduleCluster(@RequestParam int id){
        try {
            boolean isRemove = scheduleClusterService.removeById(id);
            if(isRemove){
                return JsonResponse.success("删除集群节点成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除集群节点失败");
    }





    @GetMapping("/getScheduleBaseInfo")
    public JsonResponse<ScheduleBaseInfoVo> getScheduleBaseInfo(){

        try {
            ScheduleBaseInfoVo scheduleBaseInfoVo = scheduleCenterService.getScheduleBaseInfo();
            return JsonResponse.success(scheduleBaseInfoVo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取调度基本信息失败");
    }


    @GetMapping("/getTaskSchedules")
    public JsonResponse<List<ScheduleInfoVo>> getTaskSchedules(@RequestParam String taskName){
        try {
            return JsonResponse.success(scheduleCenterService.getTaskSchedules(taskName));
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取任务调度列表信息失败");
    }


    @GetMapping("/getTaskSchedule")
    public JsonResponse<ScheduleInfoVo> getTaskSchedule(@RequestParam String scheduleName){
        try {
            return JsonResponse.success(scheduleCenterService.getTaskSchedule(scheduleName));
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取任务调度信息失败");
    }


    @PostMapping("/genTaskSchedule")
    public JsonResponse<String> genTaskSchedule(@RequestBody GeneralScheduleInfoVo scheduleInfo){
        try {
            if(scheduleCenterService.genTaskSchedule(scheduleInfo)) {
                return JsonResponse.success("生成任务新调度任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("生成任务新调度任务失败");
    }


    @PostMapping("/updateTaskSchedule")
    public JsonResponse<String> updateTaskSchedule(@RequestBody GeneralScheduleInfoVo scheduleInfo){
        try {
            if(scheduleCenterService.updateTaskSchedule(scheduleInfo)) {
                return JsonResponse.success("更新任务调度任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("更新任务调度任务失败");
    }


    @PostMapping("/deleteTaskSchedule")
    public JsonResponse<String> deleteTaskSchedule(@RequestBody String scheduleName){
        try {
            if(scheduleCenterService.deleteTaskSchedule(scheduleName)) {
                return JsonResponse.success("删除任务调度任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除任务调度任务失败");
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
        }
        return JsonResponse.fail("获取调度依赖任务失败");
    }


}
