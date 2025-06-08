package com.caesar.controller;

import com.caesar.entity.CaesarTaskTestCase;
import com.caesar.entity.dto.CaesarReviewTaskDto;
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
            if(null == currentTaskInfo){
                return JsonResponse.fail("任务信息查询失败,请确认信息重新提交.");
            }


            Boolean isPassedTest = taskExecuteService.validateTaskIsPassedTest(currentTaskInfo.getId());
            if (null == isPassedTest || !isPassedTest) {
                return JsonResponse.fail("当前发布任务没有正确完成测试,请先完成测试后重新发布任务.");
            }


//            Boolean isDeploySchedule = scheduleCenterService.validateTaskDeploySchedule(currentTaskInfo.getTaskName());
//            if (null == isDeploySchedule || !isDeploySchedule) {
//                return JsonResponse.fail("当前发布任务没有部署调度,请先完成调度部署,然后重新发布任务.");
//            }


            Boolean isPublish = developCenterService.validateTaskPublish(currentTaskInfo.getId());
            if(null != isPublish && isPublish){
                return JsonResponse.fail("当前任务已经发布,不能重复发布任务.");
            }


            Boolean isReviewing = reviewService.validateTaskReviewing(currentTaskInfo.getId());
            if(null != isReviewing && isReviewing){
                return JsonResponse.fail("当前任务正在Review,不能重复发布任务.");
            }


            CaesarTaskPublishDto publishDto = BeanConverterTools.convert(currentTaskInfo, CaesarTaskPublishDto.class);
            publishDto.setTaskId(currentTaskInfo.getId());
            publishDto.setGroupId(currentTaskInfo.getGroupId());
            publishDto.setSubmitUsername(publishVo.getSubmitUsername());
            publishDto.setCodeDesc(publishVo.getCodeDesc());

            if(publishService.publishTask(publishDto)) {
                return JsonResponse.success("发布任务成功,等待审核上线.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("发布任务失败,请检查后重试.");
    }


    @GetMapping("/getReviewTaskList")
    public JsonResponse<List<CaesarReviewTaskDto>> getReviewTaskList(
            @RequestParam String loginUser,
            @RequestParam String taskName,
            @RequestParam String reviewStatus,
            @RequestParam String reviewLevel
    ) {
        try {
            Integer loginUserId = userManagerService.getUserIdFromUserName(loginUser);
            Integer sendStatus = null;
            Integer sendLevel = null;
            if(null != reviewStatus && !"".equals(reviewStatus)){
                sendStatus = Integer.valueOf(reviewStatus);
            }
            if(null != reviewLevel && !"".equals(reviewLevel)){
                sendLevel = Integer.valueOf(reviewLevel);
            }
            List<CaesarReviewTaskDto> caesarReviewTaskDtos =  reviewService.getReviewTasks(loginUserId,taskName,sendStatus,sendLevel);

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

            // 1 调度生效
//            scheduleCenterService.releaseTask(taskId);

            // 2 更新任务记录
            developCenterService.taskPassReview2Online(taskId);

            // 3 更新数据记录
            Boolean review = reviewService.review(id, taskId, reviewStatus, reviewResult, auditMessage);

            if(review){
                return JsonResponse.success("审核成功.");
            }

//            if (ReviewLevel.FINAL == ReviewLevel.fromKey(reviewLevel)) {
//                CaesarTask onlineTask = developCenterService.getTaskOnlineVersionInfoFromReviewTaskId(taskId);
//                if (null != onlineTask && onlineTask.getId() > 0) {
//                    developCenterService.currentVersionTaskOffline(onlineTask.getId());
//                }
//                developCenterService.taskPassReview2Online(taskId);
//                Cache.codeReviewCache.remove(taskId);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("审核失败.");
    }

}
