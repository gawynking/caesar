package com.caesar.core.review;


public class InitialReviewHandler extends ReviewHandler {

    public InitialReviewHandler(ReviewLevel level) {
        super(level);
    }

    @Override
    public void handleRequest(ReviewRequest request) {
        if (super.level == ReviewLevel.INITIAL) {
            // 处理初审逻辑
            System.out.println("Initial review completed.");
        }
        if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}