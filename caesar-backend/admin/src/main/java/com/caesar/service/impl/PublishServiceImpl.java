package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.core.CodeReviewProcess;
import com.caesar.core.review.ReviewHandler;
import com.caesar.core.review.ReviewRequest;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskReviewConfig;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.TaskPublishDto;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.TaskReviewConfigMapper;
import com.caesar.mapper.TaskReviewRecordMapper;
import com.caesar.service.PublishService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Override
    public Boolean publishTask(TaskPublishDto publishDto) {
        try {

            /**
             * 如果任务已经上线则无需重复发布
             */
            if(taskMapper.checkTaskVersionIsOnline(publishDto.getTaskId())){
                return false;
            }

            /**
             * 如果任务在审核中，不能重复提交
             */
            if(taskReviewRecordMapper.taskAlreadySubmitted(publishDto.getTaskId())){
                return false;
            }

            List<CaesarTaskReviewConfig> taskReviewConfigList = taskReviewConfigMapper.getCodeReviewConfig(publishDto.getGroupId(), publishDto.getTaskType());

            for(CaesarTaskReviewConfig taskReviewConfig:taskReviewConfigList){
                CaesarTaskReviewRecord taskReviewRecord = new CaesarTaskReviewRecord();
                taskReviewRecord.setUuid(UUID.randomUUID().toString().toLowerCase().replaceAll("-",""));
                taskReviewRecord.setTaskId(publishDto.getTaskId());
                taskReviewRecord.setReviewLevel(taskReviewConfig.getReviewLevel());
                taskReviewRecord.setReviewUser(taskReviewConfig.getReviewUserId());
                taskReviewRecord.setReviewStatus(1);
                taskReviewRecord.setReviewResult(0);
                taskReviewRecordMapper.insert(taskReviewRecord);
                taskReviewConfig.setUuid(taskReviewRecord.getUuid());
            }

            ReviewHandler reviewHandler = CodeReviewProcess.generalCodeReviewChain(taskReviewConfigList);
            ReviewRequest reviewRequest = new ReviewRequest(
                    publishDto.getTaskId(),
                    publishDto.getTaskName(),
                    publishDto.getVersion(),
                    publishDto.getSubmitUsername(),
                    publishDto.getCodeDesc()
            );
            CodeReviewProcess.handleRequest(reviewHandler,reviewRequest);
//            Cache.codeReviewCache.put(publishDto.getTaskId(), reviewHandler);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
