package com.caesar.core.review;

import com.caesar.core.cache.Cache;
import com.caesar.entity.CaesarTaskReviewConfig;
import com.caesar.entity.dto.CaesarGroupReviewConfig;
import com.caesar.mapper.TaskReviewRecordMapper;

import javax.annotation.Resource;
import java.io.Serializable;


public abstract class ReviewHandler implements Serializable {

    @Resource
    TaskReviewRecordMapper reviewRecordMapper;

    protected ReviewLevel level;

    protected ReviewHandler nextHandler;

    protected CaesarGroupReviewConfig taskReviewConfig;

    ReviewRequest request;

    public ReviewHandler(ReviewLevel level, CaesarGroupReviewConfig taskReviewConfig) {
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
