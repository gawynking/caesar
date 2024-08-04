package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.core.CodeReviewProcess;
import com.caesar.core.cache.Cache;
import com.caesar.core.review.ReviewHandler;
import com.caesar.core.review.ReviewLevel;
import com.caesar.core.review.ReviewRequest;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.CaesarGroupReviewConfig;
import com.caesar.entity.dto.CaesarReviewTaskDto;
import com.caesar.entity.dto.CaesarTaskPublishDto;
import com.caesar.entity.state.ReviewState;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.TaskReviewConfigMapper;
import com.caesar.mapper.TaskReviewRecordMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.service.PublishService;
import com.caesar.service.ReviewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;


@Service
public class ReviewServiceImpl extends ServiceImpl<TaskReviewRecordMapper, CaesarTaskReviewRecord> implements ReviewService {

    @Resource
    TaskReviewRecordMapper taskReviewRecordMapper;

    @Override
    public List<CaesarReviewTaskDto> getReviewTaskListByUserId(int loginUserId) {
        return taskReviewRecordMapper.getReviewTaskListByUserId(loginUserId);
    }

    @Override
    public Boolean review(int id, int taskId, int reviewStatus, int reviewResult, String auditMessage) {
        return taskReviewRecordMapper.review(id,taskId,reviewStatus,reviewResult,auditMessage);
    }

}
