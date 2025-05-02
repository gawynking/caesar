package com.caesar.entity.state;

import com.caesar.core.review.ReviewHandler;
import com.caesar.core.review.ReviewLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewState {

    private int taskId;
    private String reviewBatch;
    private ReviewLevel step;
    private ReviewHandler handler;

}
