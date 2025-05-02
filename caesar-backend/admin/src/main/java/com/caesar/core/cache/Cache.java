package com.caesar.core.cache;

import com.caesar.entity.state.ReviewState;
import com.caesar.mapper.TaskReviewRecordMapper;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache{


    /**
     * 审核流程缓存
     */
    public static final Map<Integer, ReviewState> codeReviewCache = new ConcurrentHashMap();



}
