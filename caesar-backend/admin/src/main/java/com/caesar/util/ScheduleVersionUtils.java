package com.caesar.util;

import com.caesar.mapper.ScheduleConfigMapper;
import com.caesar.mapper.TaskMapper;

public class ScheduleVersionUtils {

    private ScheduleConfigMapper scheduleConfigMapper;

    private volatile static ScheduleVersionUtils instance;

    private ScheduleVersionUtils(ScheduleConfigMapper scheduleConfigMapper) {
        this.scheduleConfigMapper = scheduleConfigMapper;
    }

    public static ScheduleVersionUtils getInstance(ScheduleConfigMapper scheduleConfigMapper) {
        if (instance == null) {
            synchronized (ScheduleVersionUtils.class) {
                if (instance == null) {
                    instance = new ScheduleVersionUtils(scheduleConfigMapper);
                }
            }
        }
        return instance;
    }

    private volatile Integer version;

    /**
     * 获取当前分配版本号（线程安全）
     *
     * @return 当前版本号加1
     */
    public synchronized Integer getVersion() {
        if (null == version) {
            version = scheduleConfigMapper.getVersion();
            if(null == version){
                version = 0;
            }
        }
        version = version + 1;
        return version;
    }

}
