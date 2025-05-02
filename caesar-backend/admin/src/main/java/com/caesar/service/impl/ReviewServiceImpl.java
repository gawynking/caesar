package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.CaesarReviewTaskDto;
import com.caesar.mapper.TaskReviewRecordMapper;
import com.caesar.service.ReviewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


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
