package com.caesar.controller;

import com.caesar.entity.CaesarScheduleCluster;
import com.caesar.entity.dto.CaesarScheduleConfigDto;
import com.caesar.entity.vo.CaesarScheduleClusterVo;
import com.caesar.entity.vo.request.GeneralScheduleInfoVo;
import com.caesar.entity.vo.response.ScheduleBaseInfoVo;
import com.caesar.entity.vo.response.ScheduleInfoVo;
import com.caesar.entity.vo.response.TaskDependency;
import com.caesar.exception.CaesarScheduleConfigSyncException;
import com.caesar.exception.SqlParseException;
import com.caesar.model.JsonResponse;
import com.caesar.service.ScheduleCenterService;
import com.caesar.service.ScheduleClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/scheduler")
public class ScheduleCenterController {

    private static final Logger logger = Logger.getLogger(ScheduleCenterController.class.getName());

    @Autowired
    ScheduleCenterService scheduleCenterService;

    @Autowired
    ScheduleClusterService scheduleClusterService;



    // 调度管理信息
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





    // 调度配置信息
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


    @GetMapping("/getTaskSchedule")
    public JsonResponse<ScheduleInfoVo> getTaskSchedule(@RequestParam String scheduleName){
        try {
            return JsonResponse.success(scheduleCenterService.getTaskSchedule(scheduleName));
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取任务调度信息失败");
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


    @GetMapping("/getTaskDependencies")
    public JsonResponse<List<TaskDependency>> getTaskDependencies(
            @RequestParam String taskName,
            @RequestParam Integer taskVersion,
            @RequestParam String period){
        try {
            return JsonResponse.success(scheduleCenterService.getTaskDependencies(taskName,taskVersion,period));
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
        return JsonResponse.fail("获取调度依赖任务失败");
    }



    @PostMapping("/genTaskSchedule")
    public JsonResponse<String> genTaskSchedule(@RequestBody GeneralScheduleInfoVo scheduleInfo){
        try {

            String scheduleName = scheduleInfo.getScheduleName();
            CaesarScheduleConfigDto scheduleConfigDto = scheduleCenterService.findScheduleConfigFromScheduleName(scheduleName);
            if(null != scheduleConfigDto){
                logger.info(String.format("创建调度已经存在配置,专为更新: 入参 %s; 已存在配置: %s",scheduleInfo,scheduleConfigDto));
                return this.updateTaskSchedule(scheduleInfo);
            }

            if(scheduleCenterService.genTaskSchedule(scheduleInfo)) {
                return JsonResponse.success("生成新调度任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("生成新调度任务失败");
    }


    @PostMapping("/updateTaskSchedule")
    public JsonResponse<String> updateTaskSchedule(@RequestBody GeneralScheduleInfoVo scheduleInfo){
        try {

            String scheduleName = scheduleInfo.getScheduleName();
            CaesarScheduleConfigDto scheduleConfigDto = scheduleCenterService.findScheduleConfigFromScheduleName(scheduleName);
            if(null == scheduleConfigDto){
                logger.info(String.format("更新调度不存在配置,转为创建任务: 入参 %s ",scheduleInfo));
                return this.genTaskSchedule(scheduleInfo);
            }

            if(scheduleCenterService.updateTaskSchedule(scheduleInfo)) {
                return JsonResponse.success("更新调度任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("更新调度任务失败");
    }


    @PostMapping("/deleteTaskSchedule")
    public JsonResponse<String> deleteTaskSchedule(@RequestBody String scheduleName){
        try {

            CaesarScheduleConfigDto scheduleConfigDto = scheduleCenterService.findScheduleConfigFromScheduleName(scheduleName);
            if(null == scheduleConfigDto){
                logger.info(String.format("删除任务不存在,入参: %s",scheduleName));
                return JsonResponse.fail("删除调度任务不存在");
            }


            if(scheduleCenterService.deleteTaskSchedule(scheduleName)) {
                return JsonResponse.success("删除调度任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除调度任务失败");
    }


    /**
     * 用于手动同步
     *
     * @return
     */
    @PostMapping("/syncCaesarSchedulerConfig")
    public JsonResponse<Boolean> syncCaesarSchedulerConfig(){
        try {
            if(scheduleCenterService.syncCaesarSchedulerConfig().size()>0){
                return JsonResponse.success(true);
            }
        }catch (CaesarScheduleConfigSyncException e){
            e.printStackTrace();
        }

        return JsonResponse.fail();
    }

}
