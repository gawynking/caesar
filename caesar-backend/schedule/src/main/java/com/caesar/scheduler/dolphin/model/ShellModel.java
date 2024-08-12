package com.caesar.scheduler.dolphin.model;


import com.alibaba.fastjson.JSONObject;
import com.caesar.util.JSONUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ShellModel extends BaseModel{

    // task唯一编码
    Long code;

    // 节点名称
    String name;

    // 任务参数
    TaskParams taskParams = new TaskParams();

    // 任务优先级
    String taskPriority = "MEDIUM";

    // work分组
    String workerGroup = "default";

    // 失败重试间隔(分)
    int failRetryInterval = 1;

    // 失败重试次数
    int failRetryTimes = 3;

    // 描述
    String description = "";



    // 延迟执行时间
    int delayTime = 0;

    // 环境名称
    int environmentCode = -1;

    // 运行标识
    String flag = "YES";

    // 缓存执行
    String isCache = "NO";

    // 任务类型
    String taskType = "SHELL";

    // 超时时长(分)
    int timeout = 0;

    // 超时告警
    String timeoutFlag = "CLOSE";

    // 超时策略
    String timeoutNotifyStrategy = "";

    // CPU配额
    int cpuQuota = -1;

    // 内存配额
    int memoryMax = -1;

    // 任务执行类型
    String taskExecuteType = "BATCH";


    @Override
    protected BaseModel cloneSelf() {
        try {
            ShellModel taskModel = (ShellModel) super.clone();
            taskModel.taskParams = new TaskParams();
            return taskModel;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = JSONUtils.getJSONObject();
        jsonObject.put("code",code);
        jsonObject.put("name",name);
        jsonObject.put("taskPriority",taskPriority);
        jsonObject.put("workerGroup",workerGroup);
        jsonObject.put("failRetryInterval",failRetryInterval);
        jsonObject.put("failRetryTimes",failRetryTimes);
        jsonObject.put("description",description);
        jsonObject.put("delayTime",delayTime);
        jsonObject.put("environmentCode",environmentCode);
        jsonObject.put("flag",flag);
        jsonObject.put("isCache",isCache);
        jsonObject.put("taskType",taskType);
        jsonObject.put("timeout",timeout);
        jsonObject.put("timeoutFlag",timeoutFlag);
        jsonObject.put("timeoutNotifyStrategy",timeoutNotifyStrategy);
        jsonObject.put("cpuQuota",cpuQuota);
        jsonObject.put("memoryMax",memoryMax);
        jsonObject.put("taskExecuteType",taskExecuteType);
        jsonObject.put("taskParams",taskParams.toJSONObject());
        return jsonObject;
    }


    @Data
    public static class TaskParams extends BaseModel{
        // 自定义参数
        List<String> localParams = new ArrayList<>();
        // 任务脚本
        String rawScript;
        // 资源
        List<String> resourceList = new ArrayList<>();


        @Override
        protected BaseModel cloneSelf() {
            return null;
        }

        @Override
        public JSONObject toJSONObject() {
            JSONObject jsonObject = JSONUtils.getJSONObject();
            jsonObject.put("localParams",JSONUtils.getJSONArrayFromList(localParams));
            jsonObject.put("resourceList",JSONUtils.getJSONArrayFromList(resourceList));
            jsonObject.put("rawScript",rawScript);
            return jsonObject;
        }
    }

}
