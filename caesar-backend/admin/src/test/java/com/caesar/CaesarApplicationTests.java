package com.caesar;

import com.caesar.entity.bo.CaesarScheduleConfigInfoBo;
import com.caesar.mapper.ScheduleConfigMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class CaesarApplicationTests {

	@Resource
	ScheduleConfigMapper scheduleConfigMapper;


	@Test
	public void test01(){
		List<CaesarScheduleConfigInfoBo> allCaesarSystemSchedulerConfigs = scheduleConfigMapper.getAllCaesarSystemSchedulerConfigs();
		System.out.println(allCaesarSystemSchedulerConfigs);
	}



}
