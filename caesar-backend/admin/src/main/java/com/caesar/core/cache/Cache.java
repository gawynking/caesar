package com.caesar.core.cache;

import com.caesar.core.review.ReviewHandler;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

    /**
     * 审核流程缓存
     */
    public static final Map<Integer,ReviewHandler> codeReviewCache = new ConcurrentHashMap();





}
