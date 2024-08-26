package com.caesar.scheduler.dolphin.model;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caesar.util.JSONUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Data
public class DependenceModel extends BaseModel{

    // 任务编码
    Long code;

    // 延时执行时间
    int delayTime = 0;

    // 描述
    String description = "";

    // 环境编码
    Integer environmentCode = -1;

    // 失败重试间隔(分)
    int failRetryInterval = 1;

    // 失败重试次数
    int failRetryTimes = 3;

    // 运行标志
    String flag = "YES";

    // 缓存执行
    String isCache = "NO";

    // 任务名称
    String name;

    // 任务参数
    TaskParams taskParams = new TaskParams();

    // 优先级
    String taskPriority = "MEDIUM";

    // 任务类型
    String taskType = "DEPENDENT";

    // 超时时间
    Integer timeout = 0;

    // 超时告警
    String timeoutFlag = "CLOSE";

    // 超时告警策略
    String timeoutNotifyStrategy = "";

    // 工作组
    String workerGroup = "default";

    // CPU配额
    Integer cpuQuota = -1;

    // 内存配额
    Integer memoryMax = -1;

    // 任务执行类型
    String taskExecuteType = "BATCH";


    @Override
    protected BaseModel cloneSelf() {
        try{
            DependenceModel dependence = (DependenceModel)super.clone();
            dependence.taskParams = new TaskParams();
            return dependence;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = JSONUtils.getJSONObject();
        jsonObject.put("code",code);
        jsonObject.put("delayTime",delayTime);
        jsonObject.put("description",description);
        jsonObject.put("environmentCode",environmentCode);
        jsonObject.put("failRetryInterval",failRetryInterval);
        jsonObject.put("failRetryTimes",failRetryTimes);
        jsonObject.put("flag",flag);
        jsonObject.put("isCache",isCache);
        jsonObject.put("name",name);
        jsonObject.put("taskPriority",taskPriority);
        jsonObject.put("taskType",taskType);
        jsonObject.put("timeout",timeout);
        jsonObject.put("timeoutFlag",timeoutFlag);
        jsonObject.put("timeoutNotifyStrategy",timeoutNotifyStrategy);
        jsonObject.put("workerGroup",workerGroup);
        jsonObject.put("cpuQuota",cpuQuota);
        jsonObject.put("memoryMax",memoryMax);
        jsonObject.put("taskExecuteType",taskExecuteType);
        jsonObject.put("taskParams",taskParams.toJSONObject());
        return jsonObject;
    }


    @Data
    public static class TaskParams extends BaseModel{

        // 本地变量 默认[]
        List<String> localParams = new ArrayList<>();
        // 资源列表 默认[]
        List<String> resourceList = new ArrayList<>();

        private Dependence dependence = new Dependence();

        @Override
        protected BaseModel cloneSelf() {
            try{
                TaskParams taskParams = (TaskParams)super.clone();
                taskParams.localParams = new ArrayList<>();
                taskParams.resourceList = new ArrayList<>();
                taskParams.dependence = new Dependence();
                return taskParams;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public JSONObject toJSONObject() {
            JSONObject jsonObject = JSONUtils.getJSONObject();
            jsonObject.put("localParams",JSONUtils.getJSONArrayFromList(localParams));
            jsonObject.put("resourceList",JSONUtils.getJSONArrayFromList(resourceList));
            jsonObject.put("dependence",dependence.toJSONObject());
            return jsonObject;
        }
    }

    @Data
    public static class Dependence extends BaseModel{
        // 检查间隔 10s
        int checkInterval = 10;
        // 失败策略
        String failurePolicy = "DEPENDENT_FAILURE_FAILURE";

        // 关系 AND | OR
        String relation = "AND";
        List<DependTask> dependTaskList = Arrays.asList(new DependTask());


        @Override
        protected BaseModel cloneSelf() {
            try{
                Dependence dependence = (Dependence)super.clone();
                dependence.dependTaskList = new ArrayList<>();
                dependence.dependTaskList.add(new DependTask());
                return dependence;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public JSONObject toJSONObject() {
            JSONObject jsonObject = JSONUtils.getJSONObject();
            jsonObject.put("checkInterval",checkInterval);
            jsonObject.put("failurePolicy",failurePolicy);
            jsonObject.put("relation",relation);
            JSONArray dependTaskListJSONArray = JSONUtils.getJSONArray();
            for(DependTask dependTask:dependTaskList){
                dependTaskListJSONArray.add(dependTask.toJSONObject());
            }
            jsonObject.put("dependTaskList",dependTaskListJSONArray);
            return jsonObject;
        }
    }


    @Data
    public static class DependTask extends BaseModel{
        // 关系 AND | OR
        String relation = "AND";
        List<DependItem> dependItemList = new ArrayList<>();

        @Override
        protected BaseModel cloneSelf() {
            try{
                DependTask dependTask = (DependTask)super.clone();
                dependTask.dependItemList = new ArrayList<>();
                return dependTask;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public JSONObject toJSONObject() {
            JSONObject jsonObject = JSONUtils.getJSONObject();
            jsonObject.put("relation",relation);
            JSONArray dependItemListJSONArray = JSONUtils.getJSONArray();
            for(DependItem dependItem:dependItemList){
                dependItemListJSONArray.add(dependItem.toJSONObject());
            }
            jsonObject.put("dependItemList",dependItemListJSONArray);
            return jsonObject;
        }
    }

    @Data
    public static class DependItem extends BaseModel{
        // 依赖项目编码
        long projectCode;
        // 依赖工作流编码
        long definitionCode;
        // 依赖任务编码
        int depTaskCode;
        // 周期
        String cycle = "day";
        // 周期值 today:今天 last1Days:昨天
        String dateValue = "today";
        // 状态
        String state = null;

        @Override
        protected BaseModel cloneSelf() {
            try{
                return (DependItem)super.clone();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public JSONObject toJSONObject() {
            JSONObject jsonObject = JSONUtils.getJSONObject();
            jsonObject.put("projectCode",projectCode);
            jsonObject.put("definitionCode",definitionCode);
            jsonObject.put("depTaskCode",depTaskCode);
            jsonObject.put("cycle",cycle);
            jsonObject.put("dateValue",dateValue);
            jsonObject.put("state",state);
            return jsonObject;
        }
    }

}
