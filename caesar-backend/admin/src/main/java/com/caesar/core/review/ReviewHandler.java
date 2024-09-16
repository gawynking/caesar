package com.caesar.core.review;

import com.caesar.entity.dto.CaesarGroupReviewConfigDto;
import com.caesar.mapper.TaskReviewRecordMapper;

import javax.annotation.Resource;
import java.io.Serializable;


public abstract class ReviewHandler implements Serializable {

    @Resource
    TaskReviewRecordMapper reviewRecordMapper;

    protected ReviewLevel level;

    protected ReviewHandler nextHandler;

    protected CaesarGroupReviewConfigDto taskReviewConfig;

    ReviewRequest request;

    public ReviewHandler(ReviewLevel level, CaesarGroupReviewConfigDto taskReviewConfig) {
        this.level = level;
        this.taskReviewConfig = taskReviewConfig;
    }

    public int getReviewLevelKey(){
        return level.getKey();
    }

    public void setNextHandler(ReviewHandler nextHandler) {
        this.nextHandler = nextHandler;
    }


    public abstract void handleRequest(ReviewRequest request);

    public void handler(){
        handleRequest(this.request);
    }

}
