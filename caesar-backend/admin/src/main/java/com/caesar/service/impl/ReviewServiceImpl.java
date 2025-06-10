package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.CaesarReviewTaskDto;
import com.caesar.mapper.TaskMapper;
import com.caesar.mapper.TaskReviewRecordMapper;
import com.caesar.mapper.UserMapper;
import com.caesar.service.ReviewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ReviewServiceImpl extends ServiceImpl<TaskReviewRecordMapper, CaesarTaskReviewRecord> implements ReviewService {

    @Resource
    TaskReviewRecordMapper taskReviewRecordMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    TaskMapper taskMapper;

    @Override
    public List<CaesarReviewTaskDto> getReviewTaskListByUserId(int loginUserId) {
        return taskReviewRecordMapper.getReviewTaskListByUserId(loginUserId);
    }

    @Override
    public Boolean review(int id, int taskId, int reviewStatus, int reviewResult, String auditMessage) {
        return taskReviewRecordMapper.review(id,taskId,reviewStatus,reviewResult,auditMessage);
    }

    @Override
    public Boolean validateTaskReviewing(int taskId) {
        return taskReviewRecordMapper.taskAlreadySubmitted(taskId);
    }

    @Override
    public List<CaesarReviewTaskDto> getReviewTasks(Integer loginUser, String taskName, Integer reviewStatus, Integer reviewResult) {
        List<CaesarTaskReviewRecord> caesarTaskReviewRecords = taskReviewRecordMapper.getReviewTasks(loginUser,taskName,reviewStatus,reviewResult);

        List<CaesarReviewTaskDto> caesarReviewTaskDtos = new ArrayList<>();
        for(CaesarTaskReviewRecord reviewRecord :caesarTaskReviewRecords){
            CaesarReviewTaskDto reviewTaskDto = new CaesarReviewTaskDto();

            CaesarTask taskInfo = taskMapper.getTaskInfoFromId(reviewRecord.getTaskId());
            CaesarTask preTaskInfo = new CaesarTask();
            try {
                preTaskInfo = Optional.ofNullable(taskMapper.getTaskInfoFromId(
                        Optional.ofNullable(
                                taskMapper.getTaskIdByVersion(
                                        Optional.ofNullable(reviewRecord.getPreVersion()).orElse(0)
                                )
                        ).orElse(0)
                )).orElse(new CaesarTask());
            }catch (Exception e){
                e.printStackTrace();
                preTaskInfo = new CaesarTask();
            }
            reviewTaskDto.setId(reviewRecord.getId());
            reviewTaskDto.setTaskId(reviewRecord.getTaskId());
            reviewTaskDto.setTaskName(reviewRecord.getTaskName());
            reviewTaskDto.setVersion(reviewRecord.getTaskVersion());
            reviewTaskDto.setPreVersion(reviewRecord.getPreVersion());
            reviewTaskDto.setSubmitUsername(userMapper.getUsernameFromId(reviewRecord.getSubmitUserId()));
            reviewTaskDto.setCodeDesc(reviewRecord.getCodeDesc());
            reviewTaskDto.setReviewLevel(reviewRecord.getReviewLevel());
            reviewTaskDto.setReviewStatus(reviewRecord.getReviewStatus());
            reviewTaskDto.setReviewResult(reviewRecord.getReviewResult());
            reviewTaskDto.setCreateTime(reviewRecord.getCreateTime().toString());

            reviewTaskDto.setCurrentCode(taskInfo.getTaskScript());
            reviewTaskDto.setLastCode(preTaskInfo.getTaskScript());

            caesarReviewTaskDtos.add(reviewTaskDto);
        }

        return caesarReviewTaskDtos;
    }

}
