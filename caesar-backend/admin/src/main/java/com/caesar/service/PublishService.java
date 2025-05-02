package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskPublishDto;


public interface PublishService extends IService<CaesarTask> {


    Boolean publishTask(CaesarTaskPublishDto publishDto);


}