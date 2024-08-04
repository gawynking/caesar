package com.caesar.controller;

import com.caesar.core.cache.Cache;
import com.caesar.core.review.ReviewLevel;
import com.caesar.entity.dto.CaesarReviewTaskDto;
import com.caesar.entity.dto.CaesarTaskPublishDto;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.entity.vo.request.TaskPublishVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.*;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/review")
public class CodeReviewCenterController {


    @Autowired
    DevelopCenterService developCenterService;

    @Autowired
    TaskExecuteService taskExecuteService;

    @Autowired
    PublishService publishService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    UserManagerService userManagerService;



    @PostMapping("/publish")
    public JsonResponse<Boolean> publish(@RequestBody TaskPublishVo publishVo) {

        CaesarTaskVo currentTaskInfo = developCenterService.getCurrentTaskInfoWithVersion(publishVo.getTaskName(), publishVo.getVersion());
        Boolean isPassedTest = taskExecuteService.validateTaskIsPassedTest(currentTaskInfo.getId());
        if(null != isPassedTest && !isPassedTest){
            return JsonResponse.success();
        }
        CaesarTaskPublishDto publishDto = BeanConverterTools.convert(currentTaskInfo, CaesarTaskPublishDto.class);
        publishDto.setTaskId(currentTaskInfo.getId());
        publishDto.setGroupId(currentTaskInfo.getGroupId());
        publishDto.setSubmitUsername(publishVo.getSubmitUsername());
        publishDto.setCodeDesc(publishVo.getCodeDesc());
        return JsonResponse.success(publishService.publishTask(publishDto));
    }


    @GetMapping("/getReviewTaskList")
    public JsonResponse<List<CaesarReviewTaskDto>> getReviewTaskList(@RequestParam String loginUser) {

        Integer loginUserId = userManagerService.getUserIdFromUserName(loginUser);
        List<CaesarReviewTaskDto> caesarReviewTaskDtos = reviewService.getReviewTaskListByUserId(loginUserId);

        return JsonResponse.success(caesarReviewTaskDtos);
    }


    @GetMapping("/submit")
    public JsonResponse<Boolean> review(
            @RequestParam int id,
            @RequestParam int taskId,
            @RequestParam int reviewLevel,
            @RequestParam int reviewStatus,
            @RequestParam int reviewResult,
            @RequestParam String auditMessage
    ) {
        Boolean review = reviewService.review(id, taskId, reviewStatus, reviewResult, auditMessage);
        if(ReviewLevel.FINAL == ReviewLevel.fromKey(reviewLevel)) {
            developCenterService.taskPassReview2Online(taskId);
            Cache.codeReviewCache.remove(taskId);
        }
        return JsonResponse.success(review);
    }

}
