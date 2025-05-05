package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTaskExecutePlan;
import com.caesar.entity.CaesarTaskExecuteRecord;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;

import java.util.List;


public interface TaskExecuteService extends IService<CaesarTaskExecuteRecord> {

    Boolean saveExecutePlan(CaesarTaskExecutePlan taskExecutePlan);

    Boolean execute(CaesarTaskExecuteRecordDto taskExecuteRecordDto);

    Boolean refresh(List<CaesarTaskExecuteRecordDto> taskExecuteRecordDto);

    Boolean validateTaskIsPassedTest(int taskId);
}
