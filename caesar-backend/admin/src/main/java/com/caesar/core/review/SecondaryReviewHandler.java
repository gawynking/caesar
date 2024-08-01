package com.caesar.core.review;

public class SecondaryReviewHandler extends ReviewHandler {
    public SecondaryReviewHandler(ReviewLevel level) {
        super(level);
    }

    @Override
    public void handleRequest(ReviewRequest request) {
        if (super.level == ReviewLevel.SECONDARY) {
            // 处理复审逻辑
            System.out.println("Secondary review completed.");
        }
        if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}