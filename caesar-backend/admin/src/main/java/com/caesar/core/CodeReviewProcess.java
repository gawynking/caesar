package com.caesar.core;

import com.caesar.core.review.*;
import com.caesar.entity.dto.CaesarGroupReviewConfigDto;
import com.caesar.util.StringUtils;

import java.util.*;

public class CodeReviewProcess {


    /**
     * 一个组一个任务类型必须存在level=3的审核节点
     * @param taskReviewConfigList 审核配置列表 最多三级审核
     * @return
     */
    public static ReviewHandler generalCodeReviewChain(List<CaesarGroupReviewConfigDto> taskReviewConfigList) throws ReviewLevelNotFoundException {


        ArrayList<ReviewHandler> reviewHandlers = new ArrayList<>();

        for(CaesarGroupReviewConfigDto taskReviewConfig:taskReviewConfigList){
            if(StringUtils.isNotEmpty(taskReviewConfig.getReviewUsers())) {
                ReviewLevel reviewLevel = ReviewLevel.fromKey(taskReviewConfig.getReviewLevel());
                switch (reviewLevel) {
                    case INITIAL:
                        reviewHandlers.add(new InitialReviewHandler(ReviewLevel.INITIAL,taskReviewConfig));
                        break;
                    case SECONDARY:
                        reviewHandlers.add(new SecondaryReviewHandler(ReviewLevel.SECONDARY,taskReviewConfig));
                        break;
                    case FINAL:
                        reviewHandlers.add(new FinalReviewHandler(ReviewLevel.FINAL,taskReviewConfig));
                        break;
                    default:
                        throw new ReviewLevelNotFoundException();
                }
            }
        }

        Collections.sort(reviewHandlers, new Comparator<ReviewHandler>() {
            @Override
            public int compare(ReviewHandler o1, ReviewHandler o2) {
                return Integer.compare(o1.getReviewLevelKey(),o2.getReviewLevelKey());
            }
        });

        ReviewHandler previous = null;
        for(ReviewHandler reviewHandler:reviewHandlers){
            if(null != previous){
                previous.setNextHandler(reviewHandler);
            }
            previous = reviewHandler;
        }

        return reviewHandlers.get(0);

    }


    public static void handleRequest(ReviewHandler reviewHandler,ReviewRequest request){
        reviewHandler.handleRequest(request);
    }

}
