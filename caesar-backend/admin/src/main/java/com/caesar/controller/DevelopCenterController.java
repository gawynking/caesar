package com.caesar.controller;

import com.caesar.entity.dto.CaesarGroupServiceDto;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.entity.vo.CaesarEngineVo;
import com.caesar.entity.vo.CaesarGroupServiceVo;
import com.caesar.entity.vo.CaesarTaskParameterVo;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.entity.vo.request.AddTaskVo;
import com.caesar.entity.vo.request.TaskExecuteVo;
import com.caesar.entity.vo.response.CaesarTaskVersionVo;
import com.caesar.model.JsonResponse;
import com.caesar.model.MenuModel;
import com.caesar.model.MenuNode;
import com.caesar.service.*;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/develop")
public class DevelopCenterController {

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
        List<MenuModel> caesarMenus = new ArrayList<>();
        caesarMenus.addAll(menuManagerService.listMenuForAside());
        caesarMenus.addAll(developCenterService.listTaskToMenu(partten));
        List<MenuNode> menuNodes = MenuModel.convert(caesarMenus);
        return JsonResponse.success(menuNodes);
    }



    @PostMapping("/addTask")
    public JsonResponse<Boolean> addTask(@RequestBody AddTaskVo addTaskVo) {

        Integer menuId = menuManagerService.getMenuIdFromMenuIndex(addTaskVo.getMenuIndex());
        Integer createdUser = userManagerService.getUserIdFromUserName(addTaskVo.getCreatedUserName());
        Integer updatedUser = userManagerService.getUserIdFromUserName(addTaskVo.getUpdatedUserName());

        CaesarTaskDto caesarTaskDto = BeanConverterTools.convert(addTaskVo, CaesarTaskDto.class);
        caesarTaskDto.setMenuId(menuId);
        caesarTaskDto.setCreatedUser(createdUser);
        caesarTaskDto.setUpdatedUser(updatedUser);

        return JsonResponse.success(developCenterService.addTask(caesarTaskDto));
    }


    @PostMapping("/saveTask")
    public JsonResponse<Integer> saveTask(@RequestBody CaesarTaskVo caesarTaskVo) {
        String lastChecksum = developCenterService.getTaskChecksumFromVersion(caesarTaskVo.getTaskName(), caesarTaskVo.getLastVersion());
        String newChecksum = HashUtils.getMD5Hash(caesarTaskVo.getTaskScript());
        if (!newChecksum.equals(lastChecksum)) {
            caesarTaskVo.setChecksum(newChecksum);
            CaesarTaskDto caesarTaskDto = BeanConverterTools.convert(caesarTaskVo, CaesarTaskDto.class);
            return JsonResponse.success(developCenterService.saveTask(caesarTaskDto));
        }
        return JsonResponse.fail("noChange");
    }



    @GetMapping("/deleteTask")
    public JsonResponse<Boolean> deleteTask(@RequestParam(value = "taskName", required = false) String taskName) {
        return JsonResponse.success(developCenterService.markDeletedTaskFromTaskName(taskName));
    }


    @GetMapping("/getTaskInfos")
    public JsonResponse<List<CaesarTaskVo>> getTaskInfos(@RequestParam String taskName) {
        List<CaesarTaskVo> caesarTaskVos = developCenterService.getTaskInfos(taskName);
        return JsonResponse.success(caesarTaskVos);
    }

    @GetMapping("/getCurrentTaskInfo")
    public JsonResponse<CaesarTaskVo> getCurrentTaskInfo(@RequestParam String taskName) {
        CaesarTaskVo caesarTaskVo = developCenterService.getCurrentTaskInfo(taskName);
        return JsonResponse.success(caesarTaskVo);
    }

    @GetMapping("/getTaskVersions")
    public JsonResponse<CaesarTaskVersionVo> getTaskVersions(@RequestParam String taskName, int currentVersion) {
        CaesarTaskVersionVo taskVersions = developCenterService.getTaskVersions(taskName, currentVersion);
        return JsonResponse.success(taskVersions);
    }

    @GetMapping("/getParams")
    public JsonResponse<List<CaesarTaskParameterVo>> getParams() {
        return JsonResponse.success(developCenterService.getParams());
    }

    @GetMapping("/getCurrentTaskInfoWithVersion")
    public JsonResponse<CaesarTaskVo> getCurrentTaskInfoWithVersion(@RequestParam String taskName, int version) {
        return JsonResponse.success(developCenterService.getCurrentTaskInfoWithVersion(taskName, version));
    }

    @GetMapping("/getEngines")
    public JsonResponse<List<CaesarEngineVo>> getEngines() {
        return JsonResponse.success(engineService.getEngines());
    }

    @GetMapping("/getDbs")
    public JsonResponse<List<CaesarGroupServiceVo>> getDbs() {
        List<CaesarGroupServiceDto> dbs = groupServiceService.getDbs();
        CaesarGroupServiceVo caesarGroupServiceVo = new CaesarGroupServiceVo();
        List<CaesarGroupServiceVo> caesarGroupServiceVos = caesarGroupServiceVo.assembleData(dbs);
        return JsonResponse.success(caesarGroupServiceVos);
    }

    @PostMapping("/execute")
    public JsonResponse<Boolean> execute(@RequestBody TaskExecuteVo taskExecuteVo) {

        CaesarTaskExecuteRecordDto taskExecuteRecordDto = new CaesarTaskExecuteRecordDto();
        CaesarTaskVo currentTaskInfo = developCenterService.getCurrentTaskInfoWithVersion(taskExecuteVo.getTaskName(), taskExecuteVo.getVersion());
        taskExecuteRecordDto.setTaskId(currentTaskInfo.getId());
        taskExecuteRecordDto.setEnvironment(taskExecuteVo.getEnvironment());
        return JsonResponse.success(taskExecuteService.execute(taskExecuteRecordDto));
    }

}
