package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.core.CodeReviewProcess;
import com.caesar.core.cache.Cache;
import com.caesar.core.review.ReviewHandler;
import com.caesar.core.review.ReviewLevel;
import com.caesar.core.review.ReviewRequest;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.CaesarGroupReviewConfigDto;
import com.caesar.entity.dto.CaesarTaskPublishDto;
import com.caesar.entity.state.ReviewState;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.TaskReviewConfigMapper;
import com.caesar.mapper.TaskReviewRecordMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.service.PublishService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class PublishServiceImpl extends ServiceImpl<TaskMapper, CaesarTask> implements PublishService {

    @Resource
    TaskMapper taskMapper;

    @Resource
    TaskReviewConfigMapper taskReviewConfigMapper;

    @Resource
    TaskReviewRecordMapper taskReviewRecordMapper;

    @Resource
    UserMapper userMapper;


    @Override
    public Boolean publishTask(CaesarTaskPublishDto publishDto) {
        try {

            /**
             * 如果任务已经上线则无需重复发布
             */
            if(null == taskMapper.checkTaskVersionIsOnline(publishDto.getTaskId()) || !taskMapper.checkTaskVersionIsOnline(publishDto.getTaskId())) {

                /**
                 * 如果任务在审核中，不能重复提交
                 */
                if (
                    null == taskReviewRecordMapper.taskAlreadySubmitted(publishDto.getTaskId()) ||
                    !taskReviewRecordMapper.taskAlreadySubmitted(publishDto.getTaskId()) ||
                    null != Cache.codeReviewCache.get(publishDto.getTaskId())
                ) {
                    List<CaesarGroupReviewConfigDto> taskReviewConfigList = taskReviewConfigMapper.getCodeReviewConfig(publishDto.getGroupId(), publishDto.getTaskType());

                    Integer preVersion = null;
                    CaesarTask caesarTask = taskMapper.getOnlineTaskInfo(publishDto.getTaskName());
                    preVersion = null == caesarTask?null:caesarTask.getVersion();


                    String reviewBatch = UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
                    for (CaesarGroupReviewConfigDto taskReviewConfig : taskReviewConfigList) {
                        CaesarTaskReviewRecord taskReviewRecord = new CaesarTaskReviewRecord();
                        taskReviewRecord.setUuid(UUID.randomUUID().toString().toLowerCase().replaceAll("-", ""));
                        taskReviewRecord.setTaskId(publishDto.getTaskId());
                        taskReviewRecord.setTaskName(publishDto.getTaskName());
                        taskReviewRecord.setReviewBatch(reviewBatch);
                        taskReviewRecord.setVersion(publishDto.getVersion());
                        if(null != preVersion){
                            taskReviewRecord.setPreVersion(preVersion);
                        }
                        taskReviewRecord.setSubmitUserId(userMapper.getUserIdFromUserName(publishDto.getSubmitUsername()));
                        taskReviewRecord.setCodeDesc(publishDto.getCodeDesc());
                        taskReviewRecord.setReviewLevel(taskReviewConfig.getReviewLevel());
                        taskReviewRecord.setReviewStatus(1);
                        taskReviewRecord.setReviewResult(0);

                        List<Integer> reviewUserList = new ArrayList<>();
                        List<CaesarGroupReviewConfigDto.UserMapper> userList = taskReviewConfig.getReviewUserList();
                        for(CaesarGroupReviewConfigDto.UserMapper user:userList){
                            reviewUserList.add(user.getUserId());
                        }
                        taskReviewRecord.setReviewUsers(reviewUserList.toString().replaceAll("\\[","").replaceAll("\\]","").replaceAll("\\s",""));

                        taskReviewRecordMapper.insert(taskReviewRecord);
                        taskReviewConfig.setTaskId(publishDto.getTaskId());
                        taskReviewConfig.setUuid(taskReviewRecord.getUuid());
                        taskReviewConfig.setReviewBatch(reviewBatch);
                    }

                    ReviewHandler reviewHandler = CodeReviewProcess.generalCodeReviewChain(taskReviewConfigList);
                    ReviewRequest reviewRequest = new ReviewRequest(
                            publishDto.getTaskId(),
                            publishDto.getTaskName(),
                            publishDto.getVersion(),
                            publishDto.getSubmitUsername(),
                            publishDto.getCodeDesc()
                    );
                    CodeReviewProcess.handleRequest(reviewHandler, reviewRequest);
                    Cache.codeReviewCache.put(
                            publishDto.getTaskId(),
                            new ReviewState(
                                    publishDto.getTaskId(),
                                    reviewBatch,
                                    ReviewLevel.fromKey(reviewHandler.getReviewLevelKey()),
                                    reviewHandler
                            )
                    );
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
