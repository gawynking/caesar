package com.caesar.core.review;


import com.caesar.core.cache.Cache;
import com.caesar.entity.CaesarTaskReviewConfig;
import com.caesar.entity.dto.CaesarGroupReviewConfig;

public class InitialReviewHandler extends ReviewHandler {

    public InitialReviewHandler(ReviewLevel level, CaesarGroupReviewConfig taskReviewConfig) {
        super(level,taskReviewConfig);
    }

    @Override
    public void handleRequest(ReviewRequest request) {
        super.request = request;
        if (super.level == Cache.codeReviewCache.get(request.getTaskId()).getStep()) {



        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }

}
