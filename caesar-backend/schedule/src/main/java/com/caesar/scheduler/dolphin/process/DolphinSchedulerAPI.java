package com.caesar.scheduler.dolphin.process;


import com.alibaba.fastjson.JSONObject;
import com.caesar.util.HttpUtils;
import com.caesar.util.JSONUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 以 3.2.0 版本API为主
 */
public abstract class DolphinSchedulerAPI {

    public static final String VERSION = "3.2.0";
    protected String baseUrl;
    protected String token;


    public DolphinSchedulerAPI(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }


    /**
     * 生成任务唯一编码
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/task-definition/gen-task-codes?genNum=1
     *
     * @param genNum
     * @return
     */
    public JSONObject genTaskCodeList(Long projectCode, Integer genNum) throws IOException {
        String url = baseUrl + String.format("/projects/%d/task-definition/gen-task-codes?genNum=%d", projectCode, genNum);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url, null, token));
    }


    /**
     * 查询项目列表
     * GET
     * http://localhost:12345/dolphinscheduler/projects/list
     *
     * @return
     * @throws IOException
     */
    public JSONObject queryAllProjectList() throws IOException {

        String url = baseUrl + "/projects/list";
        String projectListString = HttpUtils.doGet(url, null, token);
        return JSONUtils.getJSONObjectFromString(projectListString);

    }


    /**
     * 创建工作流
     * POST
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition
     *
     * demo:
     * taskDefinitionJson: [{"code":14789903911744,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"0","flag":"YES","isCache":"NO","name":"task-node","taskParams":{"localParams":[],"rawScript":"echo \"---------------------------------------------\"\necho \"start task-node\"\necho ${etl_date} \necho ${start_date} \necho ${end_date} \necho \"---------------------------------------------\"","resourceList":[]},"taskPriority":"MEDIUM","taskType":"SHELL","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH"},{"code":14789919290560,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"0","flag":"YES","isCache":"NO","name":"dep-node","taskParams":{"localParams":[],"resourceList":[],"dependence":{"checkInterval":10,"failurePolicy":"DEPENDENT_FAILURE_FAILURE","relation":"AND","dependTaskList":[{"relation":"AND","dependItemList":[{"projectCode":14576322429504,"definitionCode":14620937083584,"depTaskCode":0,"cycle":"day","dateValue":"today","state":null}]}]}},"taskPriority":"MEDIUM","taskType":"DEPENDENT","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH"}]
     * taskRelationJson: [{"name":"","preTaskCode":0,"preTaskVersion":0,"postTaskCode":14789919290560,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}},{"name":"","preTaskCode":14789919290560,"preTaskVersion":0,"postTaskCode":14789903911744,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}}]
     * locations: [{"taskCode":14789903911744,"x":216,"y":78},{"taskCode":14789919290560,"x":233,"y":179}]
     * name: caesar-single
     * executionType: PARALLEL
     * description:
     * globalParams: [{"prop":"start_date","value":"$[last_day(yyyy-MM-dd)]","direct":"IN","type":"VARCHAR"},{"prop":"end_date","value":"$[last_day(yyyy-MM-dd)]","direct":"IN","type":"VARCHAR"},{"prop":"etl_date","value":"$[last_day(yyyy-MM-dd)]","direct":"IN","type":"VARCHAR"}]
     * timeout: 0
     *
     * @param projectCode
     * @param name
     * @param description
     * @param globalParams
     * @param locations
     * @param timeout
     * @param taskRelationJson
     * @param taskDefinitionJson
     * @param otherParamsJson
     * @param executionType
     * @return
     * @throws IOException
     */
    public JSONObject createProcessDefinition(long projectCode, // *
                                              String name, // *
                                              String description,
                                              String globalParams,
                                              String locations, // *
                                              int timeout,
                                              String taskRelationJson, // *
                                              String taskDefinitionJson, // *
                                              String otherParamsJson,
                                              String executionType) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition", projectCode);
        Map<String, String> params = new HashMap<>();
        params.put("projectCode", String.valueOf(projectCode));
        params.put("name", name);
        params.put("description", description);
        params.put("globalParams", globalParams);
        params.put("locations", locations);
        params.put("timeout", String.valueOf(timeout));
        params.put("taskRelationJson", taskRelationJson);
        params.put("taskDefinitionJson", taskDefinitionJson);
        params.put("otherParamsJson", otherParamsJson);
        params.put("executionType", executionType);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPostForm(url, params, token));
    }

    public JSONObject createProcessDefinition(
            long projectCode,
            String name,
            String locations,
            String taskRelationJson,
            String taskDefinitionJson
    ) throws IOException {
        return createProcessDefinition(
                projectCode,
                name,
                "",
                "[]",
                locations,
                0,
                taskRelationJson,
                taskDefinitionJson,
                null,
                "PARALLEL"
        );
    }


    /**
     * 查询工作流信息
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14581385154880
     *
     * @return
     * @throws IOException
     */
    public JSONObject queryProcessDefinitionByCode(long projectCode,long code) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/%d", projectCode, code);
        String processDefinition = HttpUtils.doGet(url, null, token);
        return JSONUtils.getJSONObjectFromString(processDefinition);
    }

    /**
     * 根据名称查询工作流信息
     * GET
     *  http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition?pageSize=10&pageNo=1&searchVal=start
     * @return
     * @throws IOException
     */
    public JSONObject queryProcessDefinitionListPaging(
            long projectCode,
            Integer pageNo,
            Integer pageSize,
            String searchVal
    ) throws IOException{
        if(null == pageNo || pageNo == 0){
            pageNo = 1;
        }
        if(null == pageSize || 0 == pageSize){
            pageSize = 100;
        }
        String url = baseUrl + String.format("/projects/%d/process-definition?pageSize=%d&pageNo=%d&searchVal=%s", projectCode, pageSize, pageNo,searchVal);
        String processDefinition = HttpUtils.doGet(url, null, token);
        return JSONUtils.getJSONObjectFromString(processDefinition);
    }


    public JSONObject queryProcessDefinitionListPaging(
            long projectCode,
            String searchVal
    ) throws IOException{
        return queryProcessDefinitionListPaging(projectCode,null,null,searchVal);
    }



    /**
     * 更新工作流信息
     * - update process definition, with whole process definition object including task definition, task relation and location.
     * PUT
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14581385154880
     *
     * @param projectCode
     * @param code
     * @return
     * @throws IOException
     */
    public JSONObject updateProcessDefinition(long projectCode, // required = true
                                              String name, // required = true
                                              long code, // required = true
                                              String description,
                                              String globalParams,
                                              String locations,
                                              int timeout,
                                              String taskRelationJson, // required = true
                                              String taskDefinitionJson, // required = true
                                              String otherParamsJson,
                                              String executionType,
                                              String releaseState) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/%d", projectCode, code);
        Map<String, String> params = new HashMap<>();
        params.put("projectCode", String.valueOf(projectCode));
        params.put("name", name);
        params.put("code", String.valueOf(code));
        params.put("description", description);
        params.put("globalParams", globalParams);
        params.put("locations", locations);
        params.put("timeout", String.valueOf(timeout));
        params.put("taskRelationJson", taskRelationJson);
        params.put("taskDefinitionJson", taskDefinitionJson);
        params.put("otherParamsJson", otherParamsJson);
        params.put("executionType", executionType);
        params.put("releaseState", releaseState);
        String result = HttpUtils.doPut(url, params, token);
        return JSONUtils.getJSONObjectFromString(result);
    }

    public JSONObject updateProcessDefinition(long projectCode, // required = true
                                              String name, // required = true
                                              long code, // required = true
                                              String locations,
                                              String taskRelationJson, // required = true
                                              String taskDefinitionJson, // required = true
                                              String releaseState,
                                              Integer timeout,
                                              String globalParams,
                                              String description,
                                              String executionType
    ) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/%d", projectCode, code);
        Map<String, String> params = new HashMap<>();
        params.put("projectCode", String.valueOf(projectCode));
        params.put("name", name);
        params.put("code", String.valueOf(code));
        params.put("locations", locations);
        params.put("taskRelationJson", taskRelationJson);
        params.put("taskDefinitionJson", taskDefinitionJson);
        // default
        params.put("executionType",executionType);
        params.put("description",description);
        params.put("globalParams",globalParams);
        params.put("timeout",String.valueOf(timeout));
        params.put("releaseState",releaseState);
        params.put("otherParamsJson","{}");
        String result = HttpUtils.doPut(url, params, token);
        return JSONUtils.getJSONObjectFromString(result);
    }



    /**
     * 删除工作流
     * DELETE
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14581385154880
     *
     * @param projectCode
     * @param code
     * @return
     * @throws IOException
     */
    public JSONObject deleteProcessDefinitionByCode(Long projectCode, Long code) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/%d", projectCode, code);
        String result = HttpUtils.doDelete(url, null, token);
        return JSONUtils.getJSONObjectFromString(result);
    }


    /**
     * 工作流上线/下线
     * POST
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14579673180864/release
     *
     * @param projectCode
     * @param code
     * @return
     * @throws IOException
     */
    public JSONObject releaseProcessDefinition(Long projectCode, Long code, String releaseState) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/%d/release", projectCode, code);
        Map<String, String> params = new HashMap<>();
        params.put("releaseState", releaseState);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPostForm(url, params, token));
    }


    /**
     * 查询指定工作流关系
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/lineages/list
     *
     * @param projectCode
     * @return
     * @throws IOException
     */
    public JSONObject queryWorkFlowLineage(long projectCode) throws IOException {
        String url = baseUrl + String.format("/projects/%d/lineages/list",projectCode);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url,null,token));
    }


    /**
     * 根据名称查询指定工作流关系
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/lineages/14576351243712
     *
     * @param projectCode
     * @return
     * @throws IOException
     */
    public JSONObject queryWorkFlowLineageByCode(long projectCode,long workFlowCode) throws IOException {
        String url = baseUrl + String.format("/projects/%d/lineages/%d",projectCode,workFlowCode);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url,null,token));
    }



    /**
     * 创建调度
     * <p>
     * POST
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/schedules
     *
     * @param projectCode
     * @param processDefinitionCode
     * @param schedule
     * @param warningType
     * @param warningGroupId
     * @param failureStrategy
     * @param workerGroup
     * @param tenantCode
     * @param processInstancePriority
     * @return
     * @throws IOException
     */
    public JSONObject createSchedule(
            long projectCode,
            long processDefinitionCode,
            String schedule,
            String warningType,
            int warningGroupId,
            String failureStrategy,
            String workerGroup,
            String tenantCode,
//            Long environmentCode,
            String processInstancePriority
    ) throws IOException {
        String url = baseUrl + String.format("/projects/%d/schedules", projectCode);
        Map<String, String> params = new HashMap<>();
        params.put("projectCode", String.valueOf(projectCode));
        params.put("processDefinitionCode", String.valueOf(processDefinitionCode));
        params.put("schedule", schedule);
        params.put("warningType", warningType);
        params.put("warningGroupId", String.valueOf(warningGroupId));
        params.put("failureStrategy", failureStrategy);
        params.put("workerGroup", workerGroup);
        params.put("tenantCode", tenantCode);
//        params.put("environmentCode", String.valueOf(environmentCode));
        params.put("processInstancePriority", processInstancePriority);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPostForm(url, params, token));
    }


    /**
     * 修改调度
     * PUT
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/schedules/5
     *
     * @param projectCode
     * @param id
     * @param schedule
     * @param warningType
     * @param warningGroupId
     * @param failureStrategy
     * @param workerGroup
     * @param tenantCode
     * @param processInstancePriority
     * @return
     * @throws IOException
     */
    public JSONObject updateSchedule(
            long projectCode,
            Integer id,
            String schedule,
            String warningType,
            int warningGroupId,
            String failureStrategy,
            String workerGroup,
            String tenantCode,
//            Long environmentCode,
            String processInstancePriority
    ) throws IOException {
        String url = baseUrl + String.format("/projects/%d/schedules/%d", projectCode, id);
        Map<String, String> params = new HashMap<>();
        params.put("projectCode", String.valueOf(projectCode));
        params.put("id", String.valueOf(id));
        params.put("schedule", schedule);
        params.put("warningType", warningType);
        params.put("warningGroupId", String.valueOf(warningGroupId));
        params.put("failureStrategy", failureStrategy);
        params.put("workerGroup", workerGroup);
        params.put("tenantCode", tenantCode);
//        params.put("environmentCode", String.valueOf(environmentCode));
        params.put("processInstancePriority", processInstancePriority);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPut(url, params, token));
    }


    /**
     * 删除调度
     *
     * delete
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/schedules/5?scheduleId=5
     *
     * @param projectCode
     * @param id
     * @return
     */
    public JSONObject deleteScheduleById(long projectCode, int id) throws IOException {
        String url = baseUrl + String.format("/projects/%d/schedules/5?scheduleId=%d", projectCode, id);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doDelete(url, null, token));
    }


    /**
     * 查询调度信息
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/schedules?pageSize=10&pageNo=1&projectCode=14576322429504&processDefinitionCode=14626283189440
     *
     * @param projectCode
     * @param processDefinitionCode
     * @return
     * @throws IOException
     */
    public JSONObject queryScheduleListPaging(Long projectCode,Long processDefinitionCode) throws IOException{
        String url = baseUrl + String.format("/projects/%d/schedules?pageSize=10&pageNo=1&projectCode=%d&processDefinitionCode=%d",projectCode,projectCode,processDefinitionCode);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url, null, token));
    }


    /**
     * 调度上线
     * post
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/schedules/4/online
     *
     * @return
     */
    public JSONObject publishScheduleOnline(long projectCode, int id) throws IOException {
        String url = baseUrl + String.format("/projects/%d/schedules/%d/online", projectCode, id);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPostForm(url, null, token));
    }


    /**
     * 调度下线
     * post
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/schedules/4/offline
     *
     * @return
     */
    public JSONObject offlineSchedule(long projectCode, int id) throws IOException {
        String url = baseUrl + String.format("/projects/%d/schedules/%d/offline", projectCode, id);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPostForm(url, null, token));
    }


    /**
     *
     * GET
     * http://localhost:12345/dolphinscheduler/projects/list-dependent
     *
     * @return
     * @throws IOException
     */
    public JSONObject queryAllProjectListForDependent() throws IOException {
        String url = baseUrl + String.format("/projects/list-dependent");
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url, null, token));
    }


    /**
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/query-process-definition-list
     *
     * @return
     */
    public JSONObject getProcessListByProjectCode(long projectCode) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/query-process-definition-list", projectCode);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url, null, token));
    }


    /**
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/query-task-definition-list?processDefinitionCode=14578042471232
     *
     * @param projectCode
     * @param processDefinitionCode
     * @return
     * @throws IOException
     */
    public JSONObject getTaskListByProcessDefinitionCode(long projectCode, Long processDefinitionCode) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/query-task-definition-list?processDefinitionCode=%d", projectCode, processDefinitionCode);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url, null, token));
    }





    /**
     * 查询任务定义
     * GET
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/task-definition/14583311968576
     *
     * @param projectCode
     * @param code
     * @return
     * @throws IOException
     */
    public JSONObject queryTaskDefinitionDetail(long projectCode,long code) throws IOException{
        String url = baseUrl + String.format("/projects/%d/task-definition/%d",projectCode,code);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url,null,token));
    }


    /**
     * 为指定工作流创建单个任务
     * 注意：添加任务首先要下线工作流信息
     *
     * POST
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/task-definition/save-single
     * @return
     * @throws IOException
     */
    public JSONObject createTaskBindsWorkFlow(
            long projectCode,
            long processDefinitionCode,
            String taskDefinitionJsonObj,
            String upstreamCodes
    ) throws IOException{
        String url = baseUrl + String.format("/projects/%d/task-definition/save-single",projectCode);
        Map<String, String> params = new HashMap<>();
        /** processDefinitionCode格式：
         * {
         *   "code": 14815478458304,
         *   "delayTime": "0",
         *   "description": "",
         *   "environmentCode": -1,
         *   "failRetryInterval": "1",
         *   "failRetryTimes": "5",
         *   "flag": "YES",
         *   "isCache": "NO",
         *   "name": "test2",
         *   "taskParams": {
         *     "localParams": [],
         *     "rawScript": "echo aaaaaa",
         *     "resourceList": []
         *   },
         *   "taskPriority": "MEDIUM",
         *   "taskType": "SHELL",
         *   "timeout": 0,
         *   "timeoutFlag": "CLOSE",
         *   "timeoutNotifyStrategy": "",
         *   "workerGroup": "default",
         *   "cpuQuota": -1,
         *   "memoryMax": -1,
         *   "taskExecuteType": "BATCH"
         * }
         * }
         */
        params.put("processDefinitionCode",String.valueOf(processDefinitionCode));
        params.put("taskDefinitionJsonObj",taskDefinitionJsonObj);
        params.put("upstreamCodes",upstreamCodes); // 上游依赖任务列表
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPostForm(url,params,token));
    }


    /**
     * 更新存在的指定工作流
     * PUT
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/task-definition/14815532326591/with-upstream
     *
     * @param projectCode
     * @param code
     * @param taskDefinitionJsonObj
     * @param upstreamCodes
     * @return
     * @throws IOException
     */
    public JSONObject updateTaskWithUpstream(
            long projectCode,
            long code,
            String taskDefinitionJsonObj,
            String upstreamCodes
    ) throws IOException{
        String url = baseUrl + String.format("/projects/%d/task-definition/%d/with-upstream",projectCode,code);
        Map<String, String> params = new HashMap<>();
        /** taskDefinitionJsonObj格式：
         * {
         *   "code": 14815532326591,
         *   "delayTime": "0",
         *   "description": "",
         *   "environmentCode": -1,
         *   "failRetryInterval": "1",
         *   "failRetryTimes": "5",
         *   "flag": "YES",
         *   "isCache": "NO",
         *   "name": "test03",
         *   "taskGroupId": null,
         *   "taskGroupPriority": null,
         *   "taskParams": {
         *     "localParams": [],
         *     "rawScript": "echo \"aaaaaaa\"${etl_date}",
         *     "resourceList": []
         *   },
         *   "taskPriority": "MEDIUM",
         *   "taskType": "SHELL",
         *   "timeout": 0,
         *   "timeoutFlag": "CLOSE",
         *   "timeoutNotifyStrategy": "",
         *   "workerGroup": "default",
         *   "cpuQuota": -1,
         *   "memoryMax": -1,
         *   "taskExecuteType": "BATCH"
         * }
         */
        params.put("taskDefinitionJsonObj",taskDefinitionJsonObj);
        params.put("upstreamCodes",upstreamCodes); // 上游依赖任务列表
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPut(url,params,token));
    }


    /**
     * 删除指定任务
     * DELETE
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/task-definition/14815532326591
     *
     * @param projectCode
     * @param code
     * @return
     * @throws IOException
     */
    public JSONObject deleteTaskDefinitionByCode(
            long projectCode,
            long code
    ) throws IOException{
        String url = baseUrl + String.format("/projects/%d/task-definition/%d",projectCode,code);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doDelete(url,null,token));
    }


}
