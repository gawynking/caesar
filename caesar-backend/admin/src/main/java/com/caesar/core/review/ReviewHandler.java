package com.caesar.core.review;

import com.caesar.mapper.TaskReviewRecordMapper;

import javax.annotation.Resource;
import java.io.Serializable;


public abstract class ReviewHandler implements Serializable {

    @Resource
    TaskReviewRecordMapper reviewRecordMapper;

    protected ReviewLevel level;

    protected ReviewHandler nextHandler;

    public ReviewHandler(ReviewLevel level) {
        this.level = level;
    }

    public int getReviewLevelKey(){
        return level.getKey();
    }

    public void setNextHandler(ReviewHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void handleRequest(ReviewRequest request);


}
