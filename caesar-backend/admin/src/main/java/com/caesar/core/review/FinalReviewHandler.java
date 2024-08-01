package com.caesar.core.review;

public class FinalReviewHandler extends ReviewHandler {

    public FinalReviewHandler(ReviewLevel level) {
        super(level);
    }

    @Override
    public void handleRequest(ReviewRequest request) {
        if (super.level == ReviewLevel.FINAL) {
            // 处理终审逻辑
            System.out.println("Final review completed.");
        }
        if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}
