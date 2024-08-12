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

    protected String baseUrl;
    protected String token;


    public DolphinSchedulerAPI(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }


    /**
     * 生成任务唯一编码
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
     * <p>
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition
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

    public JSONObject createProcessDefinition(long projectCode, String name, String locations, String taskRelationJson, String taskDefinitionJson) throws IOException {
        return createProcessDefinition(projectCode, name, null, "[]", locations, 0, taskRelationJson, taskDefinitionJson, null, "PARALLEL");
    }


    /**
     * 查询工作流信息
     * <p>
     * Get
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14581385154880
     *
     * @return
     * @throws IOException
     */
    public JSONObject queryProcessDefinitionByCode(Long projectCode, Long code) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/%d", projectCode, code);
        String processDefinition = HttpUtils.doGet(url, null, token);
        return JSONUtils.getJSONObjectFromString(processDefinition);
    }

    /**
     * 更新工作流信息
     * PUT
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14581385154880
     *
     * @param projectCode
     * @param code
     * @return
     * @throws IOException
     */
    public JSONObject updateProcessDefinition(long projectCode, // *
                                              String name, // *
                                              long code, // *
                                              String description, String globalParams, String locations, // *
                                              int timeout, String taskRelationJson, // *
                                              String taskDefinitionJson, // *
                                              String otherParamsJson, String executionType, String releaseState) throws IOException {
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


    public JSONObject updateProcessDefinition(long projectCode, // *
                                              String name, // *
                                              long code, // *
                                              String locations, String taskRelationJson, // *
                                              String taskDefinitionJson // *
    ) throws IOException {
        return updateProcessDefinition(projectCode, name, code, null, "[]", locations, 0, taskRelationJson, taskDefinitionJson, null, "PARALLEL", "OFFLINE");
    }


    /**
     * 删除工作流
     * Delete
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
     * post
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
     * @param environmentCode
     * @param processInstancePriority
     * @return
     * @throws IOException
     */
    public JSONObject createSchedule(long projectCode, long processDefinitionCode, String schedule, String warningType, int warningGroupId, String failureStrategy, String workerGroup, String tenantCode, Long environmentCode, String processInstancePriority) throws IOException {
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
        params.put("environmentCode", String.valueOf(environmentCode));
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
     * @param environmentCode
     * @param processInstancePriority
     * @return
     * @throws IOException
     */
    public JSONObject updateSchedule(long projectCode, Integer id, String schedule, String warningType, int warningGroupId, String failureStrategy, String workerGroup, String tenantCode, Long environmentCode, String processInstancePriority) throws IOException {
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
        params.put("environmentCode", String.valueOf(environmentCode));
        params.put("processInstancePriority", processInstancePriority);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doPut(url, params, token));
    }


    /**
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
     * Get
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/lineages/list
     *
     * @return
     */
    public JSONObject queryWorkFlowLineage(long projectCode) throws IOException {
        String url = baseUrl + String.format("/projects/%d/lineages/list", projectCode);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url, null, token));
    }


    /**
     * get
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
     * get
     * http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/query-process-definition-list
     *
     * @return
     */
    public JSONObject getProcessListByProjectCode(long projectCode) throws IOException {
        String url = baseUrl + String.format("/projects/%d/process-definition/query-process-definition-list", projectCode);
        return JSONUtils.getJSONObjectFromString(HttpUtils.doGet(url, null, token));
    }


    /**
     * get
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


}
