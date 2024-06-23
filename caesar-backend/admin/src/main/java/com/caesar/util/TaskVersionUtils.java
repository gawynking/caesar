package com.caesar.util;

import com.caesar.mapper.TaskMapper;

public class TaskVersionUtils {

    private TaskMapper taskMapper;

    private volatile static TaskVersionUtils instance;

    private TaskVersionUtils(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public static TaskVersionUtils getInstance(TaskMapper taskMapper) {
        if (instance == null) {
            synchronized (TaskVersionUtils.class) {
                if (instance == null) {
                    instance = new TaskVersionUtils(taskMapper);
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
        if (version == null) {
            version = taskMapper.getVersion();
        }
        version = version + 1;
        return version;
    }

}
