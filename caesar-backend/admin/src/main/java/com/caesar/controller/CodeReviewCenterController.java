package com.caesar.controller;

import com.caesar.core.cache.Cache;
import com.caesar.core.review.ReviewLevel;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskTestCase;
import com.caesar.entity.dto.CaesarReviewTaskDto;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.dto.CaesarTaskPublishDto;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.entity.vo.request.TaskPublishVo;
import com.caesar.entity.vo.request.VerificationTestingVo;
import com.caesar.entity.vo.response.CaesarTaskTestCaseVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.*;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping("/review")
public class CodeReviewCenterController {

    private static final Logger logger = Logger.getLogger(CodeReviewCenterController.class.getName());

    @Autowired
    DevelopCenterService developCenterService;

    @Autowired
    ScheduleCenterService scheduleCenterService;

    @Autowired
    TaskExecuteService taskExecuteService;

    @Autowired
    PublishService publishService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    UserManagerService userManagerService;



    @GetMapping("/getTestCases")
    public JsonResponse<List<CaesarTaskTestCaseVo>> getTestCases(
            @RequestParam String username,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) Integer testResult) {

        try {
            List<CaesarTaskTestCase> testCases = taskExecuteService.getTestCases(
                    userManagerService.getUserIdFromUserName(username),
                    taskName,
                    testResult
            );

            List<CaesarTaskTestCaseVo> caesarTaskTestCaseVos = new ArrayList<>();
            for(CaesarTaskTestCase testCase :testCases){
                CaesarTaskTestCaseVo tmpTestCaseVo = BeanConverterTools.convert(testCase, CaesarTaskTestCaseVo.class);
                tmpTestCaseVo.setUsername(username);
                tmpTestCaseVo.setCreateTime(testCase.getCreateTime());
                caesarTaskTestCaseVos.add(tmpTestCaseVo);
            }

            return JsonResponse.success(caesarTaskTestCaseVos);
        } catch (Exception e) {
            logger.info("获取测试用例失败");
            return JsonResponse.fail("获取测试用例失败");
        }
    }


    /**
     * 验证测试
     */
    @PostMapping("/verificationTesting")
    public JsonResponse<Boolean> verificationTesting(@RequestBody VerificationTestingVo verificationTestingVo){
        Boolean testing = taskExecuteService.verificationTesting(verificationTestingVo);
        return JsonResponse.success(testing);
    }


    @PostMapping("/publish")
    public JsonResponse<String> publish(@RequestBody TaskPublishVo publishVo) {
        try {
            CaesarTaskVo currentTaskInfo = developCenterService.getCurrentTaskInfoWithVersion(publishVo.getTaskName(), publishVo.getVersion());
            Boolean isPassedTest = taskExecuteService.validateTaskIsPassedTest(currentTaskInfo.getId());
            if (null != isPassedTest && !isPassedTest) {
                return JsonResponse.success();
            }
            CaesarTaskPublishDto publishDto = BeanConverterTools.convert(currentTaskInfo, CaesarTaskPublishDto.class);
            publishDto.setTaskId(currentTaskInfo.getId());
            publishDto.setGroupId(currentTaskInfo.getGroupId());
            publishDto.setSubmitUsername(publishVo.getSubmitUsername());
            publishDto.setCodeDesc(publishVo.getCodeDesc());
            if(publishService.publishTask(publishDto)) {
                return JsonResponse.success("发布任务成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("发布任务失败");
    }


    @GetMapping("/getReviewTaskList")
    public JsonResponse<List<CaesarReviewTaskDto>> getReviewTaskList(@RequestParam String loginUser) {
        try {
            Integer loginUserId = userManagerService.getUserIdFromUserName(loginUser);
            List<CaesarReviewTaskDto> caesarReviewTaskDtos = reviewService.getReviewTaskListByUserId(loginUserId);

            return JsonResponse.success(caesarReviewTaskDtos);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取任务review列表失败");
    }


    @GetMapping("/submit")
    public JsonResponse<String> review(
            @RequestParam int id,
            @RequestParam int taskId,
            @RequestParam int reviewLevel,
            @RequestParam int reviewStatus,
            @RequestParam int reviewResult,
            @RequestParam String auditMessage
    ) {
        try {
            Boolean review = reviewService.review(id, taskId, reviewStatus, reviewResult, auditMessage);
            if (ReviewLevel.FINAL == ReviewLevel.fromKey(reviewLevel)) {
                CaesarTask onlineTask = developCenterService.getTaskOnlineVersionInfoFromReviewTaskId(taskId);
                if (null != onlineTask && onlineTask.getId() > 0) {
                    developCenterService.currentVersionTaskOffline(onlineTask.getId());
                }
                developCenterService.taskPassReview2Online(taskId);
                Cache.codeReviewCache.remove(taskId);
            }
            return JsonResponse.success("提交任务成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("提交任务失败");
    }

}
