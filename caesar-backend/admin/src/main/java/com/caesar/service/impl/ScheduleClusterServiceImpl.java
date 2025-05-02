package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.core.CodeReviewProcess;
import com.caesar.core.cache.Cache;
import com.caesar.core.review.ReviewHandler;
import com.caesar.core.review.ReviewLevel;
import com.caesar.core.review.ReviewRequest;
import com.caesar.entity.CaesarScheduleCluster;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskReviewRecord;
import com.caesar.entity.dto.CaesarGroupReviewConfigDto;
import com.caesar.entity.dto.CaesarTaskPublishDto;
import com.caesar.entity.state.ReviewState;
import com.caesar.mapper.*;
import com.caesar.service.PublishService;
import com.caesar.service.ScheduleClusterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ScheduleClusterServiceImpl extends ServiceImpl<ScheduleClusterMapper, CaesarScheduleCluster> implements ScheduleClusterService {


}
