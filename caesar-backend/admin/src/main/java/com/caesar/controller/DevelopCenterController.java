package com.caesar.controller;

import com.alibaba.fastjson.JSONObject;
import com.caesar.entity.dto.CaesarGroupServiceDto;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.entity.vo.CaesarEngineVo;
import com.caesar.entity.vo.CaesarGroupServiceVo;
import com.caesar.entity.vo.CaesarTaskParameterVo;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.entity.vo.request.AddTaskVo;
import com.caesar.entity.vo.request.TaskExecuteVo;
import com.caesar.entity.vo.request.TaskRefreshVo;
import com.caesar.entity.vo.response.CaesarTaskVersionVo;
import com.caesar.entity.vo.response.MenuDbs;
import com.caesar.model.JsonResponse;
import com.caesar.model.MenuModel;
import com.caesar.model.MenuNode;
import com.caesar.model.code.TemplateUtils;
import com.caesar.model.code.enums.DatePeriod;
import com.caesar.service.*;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.DateUtils;
import com.caesar.util.HashUtils;
import com.caesar.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@RestController
@RequestMapping("/develop")
public class DevelopCenterController {

    private static final Logger LOGGER = Logger.getLogger(DevelopCenterController.class.getName());


    @Autowired
    DevelopCenterService developCenterService;

    @Autowired
    MenuManagerService menuManagerService;

    @Autowired
    UserManagerService userManagerService;

    @Autowired
    EngineService engineService;

    @Autowired
    GroupServiceService groupServiceService;

    @Autowired
    TaskExecuteService taskExecuteService;



    @GetMapping("/listTask")
    public JsonResponse<List<MenuNode>> listTask(@RequestParam String partten) {
        try {
            LOGGER.info(String.format("输入参数 => %s",partten));
            List<MenuModel> caesarMenus = new ArrayList<>();
            caesarMenus.addAll(menuManagerService.listMenuForAside());
            caesarMenus.addAll(developCenterService.listTaskToMenu(partten));
            List<MenuNode> menuNodes = MenuModel.convert(caesarMenus);
            LOGGER.info(String.format("返回菜单列表 => %s",menuNodes.toString()));
            return JsonResponse.success(menuNodes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取任务列表失败");
    }



    @PostMapping("/addTask")
    public JsonResponse<String> addTask(@RequestBody AddTaskVo addTaskVo) {

        try {
            Integer menuId = menuManagerService.getMenuIdFromMenuIndex(addTaskVo.getMenuIndex());
            Integer createdUser = userManagerService.getUserIdFromUserName(addTaskVo.getCreatedUserName());
            Integer updatedUser = userManagerService.getUserIdFromUserName(addTaskVo.getUpdatedUserName());

            CaesarTaskDto caesarTaskDto = BeanConverterTools.convert(addTaskVo, CaesarTaskDto.class);
            caesarTaskDto.setMenuId(menuId);
            caesarTaskDto.setCreatedUser(createdUser);
            caesarTaskDto.setUpdatedUser(updatedUser);
            boolean flag = developCenterService.addTask(caesarTaskDto);
            if(flag) {
                return JsonResponse.success("添加任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("添加任务失败");
    }


    @PostMapping("/saveTask")
    public JsonResponse<String> saveTask(@RequestBody CaesarTaskVo caesarTaskVo) {
        try {
            String lastChecksum = developCenterService.getTaskChecksumFromVersion(caesarTaskVo.getTaskName(), caesarTaskVo.getLastVersion());
            String newChecksum = HashUtils.getMD5Hash(caesarTaskVo.getTaskScript());
            if (!newChecksum.equals(lastChecksum)) {
                caesarTaskVo.setChecksum(newChecksum);
                CaesarTaskDto caesarTaskDto = BeanConverterTools.convert(caesarTaskVo, CaesarTaskDto.class);
                int currentVersion = developCenterService.saveTask(caesarTaskDto);
                return JsonResponse.success(String.valueOf(currentVersion));
            }
            return JsonResponse.fail("任务没有变化");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("保存任务失败");
    }



    @GetMapping("/deleteTask")
    public JsonResponse<String> deleteTask(@RequestParam(value = "taskName", required = false) String taskName) {
        try {
            if (developCenterService.markDeletedTaskFromTaskName(taskName)) {
                return JsonResponse.success("删除任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除任务失败");
    }


    @GetMapping("/getTaskInfos")
    public JsonResponse<List<CaesarTaskVo>> getTaskInfos(@RequestParam String taskName) {
        try {
            List<CaesarTaskVo> caesarTaskVos = developCenterService.getTaskInfos(taskName);
            return JsonResponse.success(caesarTaskVos);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取任务信息失败");
    }

    @GetMapping("/getCurrentTaskInfo")
    public JsonResponse<CaesarTaskVo> getCurrentTaskInfo(@RequestParam String taskName) {
        try {
            CaesarTaskVo caesarTaskVo = developCenterService.getCurrentTaskInfo(taskName);
            return JsonResponse.success(caesarTaskVo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取当前任务信息失败");
    }

    @GetMapping("/getTaskVersions")
    public JsonResponse<CaesarTaskVersionVo> getTaskVersions(@RequestParam String taskName, @RequestParam int currentVersion) {
        try {
            CaesarTaskVersionVo taskVersions = developCenterService.getTaskVersions(taskName, currentVersion);
            return JsonResponse.success(taskVersions);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取任务版本失败");
    }


    @GetMapping("/getParams")
    public JsonResponse<List<CaesarTaskParameterVo>> getParams() {
        try {
            return JsonResponse.success(developCenterService.getParams());
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取参数失败");
    }

    @GetMapping("/getCurrentTaskInfoWithVersion")
    public JsonResponse<CaesarTaskVo> getCurrentTaskInfoWithVersion(@RequestParam String taskName, int version) {
        try {
            return JsonResponse.success(developCenterService.getCurrentTaskInfoWithVersion(taskName, version));
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取当前版本对应任务信息失败");
    }

    @GetMapping("/getEngines")
    public JsonResponse<List<CaesarEngineVo>> getEngines() {
        try {
            return JsonResponse.success(engineService.getEngines());
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取引擎列表失败");
    }

    @GetMapping("/getDbs")
    public JsonResponse<List<CaesarGroupServiceVo>> getDbs() {
        try {
            List<CaesarGroupServiceDto> dbs = groupServiceService.getDbs();
            CaesarGroupServiceVo caesarGroupServiceVo = new CaesarGroupServiceVo();
            List<CaesarGroupServiceVo> caesarGroupServiceVos = caesarGroupServiceVo.assembleData(dbs);
            return JsonResponse.success(caesarGroupServiceVos);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取数据库信息失败");
    }


    @GetMapping("/getMenuDbs")
    public JsonResponse<List<MenuDbs>> getMenuDbs() {
        try {
            List<MenuDbs> menuDbs = groupServiceService.getMenuDbs();
            return JsonResponse.success(menuDbs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取菜单对应数据库信息失败");
    }


    @PostMapping("/execute")
    public JsonResponse<String> execute(@RequestBody TaskExecuteVo taskExecuteVo) {
        try {
            CaesarTaskExecuteRecordDto taskExecuteRecordDto = new CaesarTaskExecuteRecordDto();
            CaesarTaskVo currentTaskInfo = developCenterService.getCurrentTaskInfoWithVersion(taskExecuteVo.getTaskName(), taskExecuteVo.getVersion());
            taskExecuteRecordDto.setTaskId(currentTaskInfo.getId());
            taskExecuteRecordDto.setEnvironment(taskExecuteVo.getEnvironment());
            taskExecuteService.execute(taskExecuteRecordDto);
            return JsonResponse.success("执行成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("执行失败");
    }


    @PostMapping("/refresh")
    public JsonResponse<String> refresh(@RequestBody TaskRefreshVo taskRefreshVo) {

        try {
            List<CaesarTaskExecuteRecordDto> taskExecuteRecordDtos = new ArrayList<>();
            CaesarTaskVo currentTaskInfo = developCenterService.getCurrentTaskInfoWithVersion(taskRefreshVo.getTaskName(), taskRefreshVo.getVersion());

            try {
                Date startDate = DateUtils.dateParse(taskRefreshVo.getStartDate(), "yyyy-MM-dd");
                Date endDate = DateUtils.dateParse(taskRefreshVo.getEndDate(), "yyyy-MM-dd");

                if ("day".equals(taskRefreshVo.getPeriod().toLowerCase())) {
                    while (DateUtils.dateCompare(startDate, endDate) <= 0) {
                        CaesarTaskExecuteRecordDto caesarTaskExecuteRecordDto = new CaesarTaskExecuteRecordDto();
//                        JSONObject parameter = JSONUtils.getJSONObject();
//                        parameter.put("period","day");
//                        parameter.put("etl_date", DateUtils.dateFormat(startDate, "yyyy-MM-dd"));
//                        parameter.put("start_date", DateUtils.dateFormat(startDate, "yyyy-MM-dd"));
//                        parameter.put("end_date", DateUtils.dateFormat(startDate, "yyyy-MM-dd"));
                        JSONObject parameter = JSONUtils.getJSONObjectFromMap(TemplateUtils.generalRefreshParameter(DatePeriod.fromKey("day"), startDate));
                        caesarTaskExecuteRecordDto.setParameter(parameter.toJSONString());
                        caesarTaskExecuteRecordDto.setTaskId(currentTaskInfo.getId());
                        caesarTaskExecuteRecordDto.setEnvironment(taskRefreshVo.getEnvironment());
                        taskExecuteRecordDtos.add(caesarTaskExecuteRecordDto);
                        startDate = DateUtils.dateAdd(startDate, 1, false);
                    }
                } else if ("month".equals(taskRefreshVo.getPeriod().toLowerCase())) {
                    while (DateUtils.dateCompare(startDate, endDate) <= 0) {
                        CaesarTaskExecuteRecordDto caesarTaskExecuteRecordDto = new CaesarTaskExecuteRecordDto();
//                        JSONObject parameter = JSONUtils.getJSONObject();
//                        parameter.put("period","month");
//                        parameter.put("etl_date", DateUtils.getMonthStart(startDate));
//                        parameter.put("start_date", DateUtils.getMonthStart(startDate));
//                        parameter.put("end_date", DateUtils.getMonthEnd(startDate));
                        JSONObject parameter = JSONUtils.getJSONObjectFromMap(TemplateUtils.generalRefreshParameter(DatePeriod.fromKey("month"), startDate));
                        caesarTaskExecuteRecordDto.setParameter(parameter.toJSONString());
                        caesarTaskExecuteRecordDto.setTaskId(currentTaskInfo.getId());
                        caesarTaskExecuteRecordDto.setEnvironment(taskRefreshVo.getEnvironment());
                        taskExecuteRecordDtos.add(caesarTaskExecuteRecordDto);
                        startDate = DateUtils.addMonth(startDate, 1);
                    }
                } else {
                    return JsonResponse.fail("回刷周期必须选择day或者month");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            taskExecuteService.refresh(taskExecuteRecordDtos);
            return JsonResponse.success("提交回刷任务成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("提交回刷任务失败");
    }


}
