package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.CaesarReviewTaskDto;

import java.util.List;


public interface ReviewService extends IService<CaesarTaskReviewRecord> {


    List<CaesarReviewTaskDto> getReviewTaskListByUserId(int loginUserId);

    Boolean review(int id,int taskId, int reviewStatus,int reviewResult, String auditMessage);
}