package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.TaskPublishDto;
import com.caesar.entity.vo.CaesarTaskVo;


public interface PublishService extends IService<CaesarTask> {


    Boolean publishTask(TaskPublishDto publishDto);


}