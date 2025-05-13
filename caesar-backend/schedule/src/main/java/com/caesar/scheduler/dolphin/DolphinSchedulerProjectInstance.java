package com.caesar.scheduler.dolphin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.exception.TaskCodeNotNullException;
import com.caesar.model.DependencyModel;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerInstance;
import com.caesar.scheduler.dolphin.model.*;
import com.caesar.util.DateUtils;
import com.caesar.util.JSONUtils;
import com.caesar.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * 项目粒度调度管理器
 *
 * 接口文档地址：http://127.0.0.1:12345/dolphinscheduler/swagger-ui/index.html?language=zh_CN&lang=cn&urls.primaryName=v2
 */
public class DolphinSchedulerProjectInstance extends DolphinSchedulerBaseInstance implements SchedulerInstance {

    private static final Logger LOGGER = Logger.getLogger(DolphinSchedulerProjectInstance.class.getName());

    public DolphinSchedulerProjectInstance(){
        super();
    }



    @Override
    public JSONObject deployTask(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException,TaskCodeNotNullException{

        JSONObject result = JSONUtils.getJSONObject();
        JSONArray resultData = JSONUtils.getJSONArray();
        result.put("type","deployTask");
        result.put("data",resultData);
        String name = schedulerModel.getTaskNodeName();
        String project = schedulerModel.getProject();
        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        Long projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        Long processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, name);
        if(null == processDefinitionCode){ // 创建调度
            // 1 创建任务
            JSONObject task = createTask(schedulerModel);
            // 2 获取任务Code
            processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, name);
            // 3 上线任务
            schedulerModel.setReleaseState(1);
            JSONObject release = release(schedulerModel);
            // 4 创建调度
            JSONObject schedule = timingTask(schedulerModel);
            // 5 调度上线
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            boolean scheduleOnline = publishScheduleOnline(projectCode, scheduleId);
            resultData.add(task);
            resultData.add(release);
            resultData.add(schedule);
            return result;
        }else {
            if(schedulerModel.getIsDelete()){
                result = deleteTask(schedulerModel);
                return result;
            }else {
                // 1 下线调度
                Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
                boolean scheduleOffline = publishScheduleOffline(projectCode, scheduleId);
                // 2 下线任务
                schedulerModel.setReleaseState(0);
                JSONObject offline = release(schedulerModel);
                // 3 更新任务
                JSONObject task = updateTask(schedulerModel);
                // 4 上线任务
                schedulerModel.setReleaseState(1);
                JSONObject online = release(schedulerModel);
                // 5 上线调度
                boolean scheduleOnline = publishScheduleOnline(projectCode, scheduleId);
                // 6 返回结果
                resultData.add(offline);
                resultData.add(task);
                resultData.add(online);
            }
            return result;
        }

    }



    /* demo:
     taskDefinitionJson: [{"code":14809682727872,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"0","flag":"YES","isCache":"NO","name":"level1-1","taskParams":{"localParams":[],"rawScript":"echo level1-1","resourceList":[]},"taskPriority":"MEDIUM","taskType":"SHELL","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH"},{"code":14809685243072,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"0","flag":"YES","isCache":"NO","name":"level1-2","taskParams":{"localParams":[],"rawScript":"echo level1-2","resourceList":[]},"taskPriority":"MEDIUM","taskType":"SHELL","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH"},{"code":14809687771712,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"0","flag":"YES","isCache":"NO","name":"level2","taskParams":{"localParams":[],"rawScript":"echo level2","resourceList":[]},"taskPriority":"MEDIUM","taskType":"SHELL","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH"}]
     taskRelationJson: [{"name":"","preTaskCode":0,"preTaskVersion":0,"postTaskCode":14809682727872,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}},{"name":"","preTaskCode":0,"preTaskVersion":0,"postTaskCode":14809685243072,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}},{"name":"","preTaskCode":14809682727872,"preTaskVersion":0,"postTaskCode":14809687771712,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}},{"name":"","preTaskCode":14809685243072,"preTaskVersion":0,"postTaskCode":14809687771712,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}}]
     locations: [{"taskCode":14809682727872,"x":263,"y":42},{"taskCode":14809685243072,"x":290,"y":140},{"taskCode":14809687771712,"x":608,"y":50}]
     name: caesar
     executionType: PARALLEL
     description:
     globalParams: [{"prop":"etl_date","value":"$[last_day(yyyy-MM-dd)]","direct":"IN","type":"VARCHAR"}]
     timeout: 0
     */
    @Override
    public JSONObject createTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {

        String project = schedulerModel.getProject();
        if(StringUtils.isEmpty(project)){
             throw new ProjectNotExistsException("Project不能为空");
        }

        Long projectCode = null;
        long taskCode = 0l;
        long dependenceCode = 0l;

        String name = null;
        String executionType = null;
        String description = null;
        String globalParams = "[{\"prop\":\"start_date\",\"value\":\"$[last_day(yyyy-MM-dd)]\",\"direct\":\"IN\",\"type\":\"VARCHAR\"},{\"prop\":\"end_date\",\"value\":\"$[last_day(yyyy-MM-dd)]\",\"direct\":\"IN\",\"type\":\"VARCHAR\"},{\"prop\":\"etl_date\",\"value\":\"$[last_day(yyyy-MM-dd)]\",\"direct\":\"IN\",\"type\":\"VARCHAR\"}]";
        int timeout = 0;
        String locations = null;

        String taskRelationJson = null;
        String taskDefinitionJson = null;


        projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }
        try {
            taskCode = this.schedulerAPI.genTaskCodeList(projectCode, 1).getJSONArray("data").getLong(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(taskCode == 0l){
            throw new GenTaskCodeFaildException("生成taskCode不能为空");
        }
        try {
            dependenceCode = this.schedulerAPI.genTaskCodeList(projectCode, 1).getJSONArray("data").getLong(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(dependenceCode == 0l){
            throw new GenTaskCodeFaildException("生成dependenceCode不能为空");
        }

        name = schedulerModel.getTaskNodeName();
        executionType = schedulerModel.getExecutionType();
        description = schedulerModel.getDescription();
//        globalParams = schedulerModel.getGlobalParams();
        timeout = schedulerModel.getTimeout();

        Long processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, name);
        if(null != processDefinitionCode){
            LOGGER.warning("任务名称已存在，不能重复创建");
            return null;
        }

        // locations
        JSONArray locationList = JSONUtils.getJSONArray();
        Location locationTask = (Location)BaseModel.getModelMapper().get(Location.class.getName()).getModel();
        Location locationDepend = (Location)BaseModel.getModelMapper().get(Location.class.getName()).getModel();
        locationTask.setTaskCode(taskCode);
        locationDepend.setTaskCode(dependenceCode);
        locationList.add(locationTask.toJSONObject());
        locationList.add(locationDepend.toJSONObject());
        locations = locationList.toString();


        // taskRelationJson
        JSONArray taskRelationList = JSONUtils.getJSONArray();
        TaskRelation taskRelationTask = (TaskRelation)BaseModel.getModelMapper().get(TaskRelation.class.getName()).getModel();
        TaskRelation taskRelationDepend = (TaskRelation)BaseModel.getModelMapper().get(TaskRelation.class.getName()).getModel();
        taskRelationTask.setPreTaskCode(dependenceCode);
        taskRelationTask.setPostTaskCode(taskCode);
        taskRelationDepend.setPostTaskCode(dependenceCode);
        taskRelationList.add(taskRelationTask.toJSONObject());
        taskRelationList.add(taskRelationDepend.toJSONObject());
        taskRelationJson = taskRelationList.toString();


//        taskDefinitionJson
        JSONArray taskDefinitionList = JSONUtils.getJSONArray();
        ShellModel taskDefinitionTask = (ShellModel)BaseModel.getModelMapper().get(ShellModel.class.getName()).getModel();
        DependenceModel taskDefinitionDepend = (DependenceModel)BaseModel.getModelMapper().get(DependenceModel.class.getName()).getModel();

        taskDefinitionTask.setCode(taskCode);
        taskDefinitionTask.setName(name);
        taskDefinitionTask.getTaskParams().setRawScript(schedulerModel.getExecTaskScript());

        taskDefinitionDepend.setCode(dependenceCode);
        taskDefinitionDepend.setName(name+".scheduler");

        List<DependencyModel> dependencys = schedulerModel.getDependency();
        List<DependenceModel.DependItem> dependItemList = new ArrayList<>();
        for(DependencyModel dependency:dependencys) {

            try {
                JSONObject processDefinitionListPaging = this.schedulerAPI.queryProcessDefinitionListPaging(projectCode, dependency.getDependency());
                if("success".equals(processDefinitionListPaging.getString("msg"))){
                    JSONArray processDefinitionList = processDefinitionListPaging.getJSONObject("data").getJSONArray("totalList");
                    DependenceModel.DependItem dependItem = new DependenceModel.DependItem();
                    for(int i=0; i<processDefinitionList.size(); i++){
                        JSONObject processDefinition = processDefinitionList.getJSONObject(i);
                        if(StringUtils.isNotEmpty(dependency.getDependency()) && dependency.getDependency().equals(processDefinition.getString("name"))){
                            dependItem.setProjectCode(projectCode);
                            dependItem.setDefinitionCode(processDefinition.getLong("code"));
                            dependItem.setDepTaskCode(0);
                            dependItem.setCycle(dependency.getPeriod().toString().toLowerCase());
                            dependItem.setDateValue(dependency.getDateValue());
                            dependItemList.add(dependItem);
                            break;
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        taskDefinitionDepend.getTaskParams().getDependence().getDependTaskList().get(0).setDependItemList(dependItemList);

        taskDefinitionList.add(taskDefinitionTask.toJSONObject());
        taskDefinitionList.add(taskDefinitionDepend.toJSONObject());
        taskDefinitionJson = taskDefinitionList.toString();


        try{
            JSONObject processDefinition = this.schedulerAPI.createProcessDefinition(
                    projectCode,
                    name,
                    locations,
                    taskRelationJson,
                    taskDefinitionJson
            );
            return processDefinition;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }



    /* demo:
        - GET - http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14622363103936
        - PUT - http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14622363103936
            taskDefinitionJson: [{"code":14625318013888,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"3","flag":"YES","isCache":"NO","name":"gawyn-test15","taskGroupId":null,"taskGroupPriority":null,"taskParams":{"localParams":[],"rawScript":"echo test15-111","resourceList":[]},"taskPriority":"MEDIUM","taskType":"SHELL","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH","version":1,"id":53},{"id":54,"code":14625318012481,"name":"gawyn-test15.scheduler","version":1,"description":"","projectCode":14576322429504,"userId":1,"taskType":"DEPENDENT","taskParams":{"dependence":{"checkInterval":10,"dependTaskList":[{"dependItemList":[{"dateValue":"today","depTaskCode":0,"projectCode":14576322429504,"definitionCode":14620937083584,"cycle":"day"}],"relation":"AND"}],"failurePolicy":"DEPENDENT_FAILURE_FAILURE","relation":"AND"},"localParams":[],"resourceList":[]},"taskParamList":[],"taskParamMap":null,"flag":"YES","isCache":"NO","taskPriority":"MEDIUM","userName":null,"projectName":null,"workerGroup":"default","environmentCode":-1,"failRetryTimes":3,"failRetryInterval":1,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":null,"timeout":0,"delayTime":0,"resourceIds":null,"createTime":"2024-08-27 13:07:14","updateTime":"2024-08-27 13:07:14","modifyBy":null,"taskGroupId":0,"taskGroupPriority":0,"cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH","operator":1,"operateTime":"2024-08-27 13:07:14","dependence":"{\"checkInterval\":10,\"dependTaskList\":[{\"dependItemList\":[{\"dateValue\":\"today\",\"depTaskCode\":0,\"projectCode\":14576322429504,\"definitionCode\":14620937083584,\"cycle\":\"day\"}],\"relation\":\"AND\"}],\"failurePolicy\":\"DEPENDENT_FAILURE_FAILURE\",\"relation\":\"AND\"}"}]
            taskRelationJson: [{"name":"","preTaskCode":0,"preTaskVersion":0,"postTaskCode":14625318012481,"postTaskVersion":1,"conditionType":"NONE","conditionParams":{}},{"name":"","preTaskCode":14625318012481,"preTaskVersion":1,"postTaskCode":14625318013888,"postTaskVersion":1,"conditionType":"NONE","conditionParams":{}}]
            locations: [{"taskCode":14625318013888,"x":29,"y":25},{"taskCode":14625318012481,"x":46,"y":68}]
            name: gawyn-test15
            executionType: PARALLEL
            description:
            globalParams: []
            timeout: 0
            releaseState: OFFLINE
     */
    @Override
    public JSONObject updateTask(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException, TaskCodeNotNullException {

        Long projectCode;
        Long processDefinitionCode; // code
        String name;
        String executionType;
        String description;
        String globalParams;
        Integer timeout;
        String releaseState;
        String locations;
        String taskRelationJson;
        String taskDefinitionJson;


        name = schedulerModel.getTaskNodeName();
        String project = schedulerModel.getProject();
        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, name);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        JSONObject processDefinition = null;
        try {
            processDefinition = this.schedulerAPI.queryProcessDefinitionByCode(projectCode, processDefinitionCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        executionType = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("executionType");
        description = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("description");
        globalParams = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("globalParams");
        timeout = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getInteger("timeout");
        releaseState = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("releaseState");
        locations = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("locations");


        JSONArray taskRelation = JSONUtils.getJSONArray();
        JSONArray processTaskRelationList = processDefinition.getJSONObject("data").getJSONArray("processTaskRelationList");
        for(int i=0; i<processTaskRelationList.size(); i++){
            JSONObject tmpTask = JSONUtils.getJSONObject();
            JSONObject tmpProcessTask = processTaskRelationList.getJSONObject(i);
            tmpTask.put("name",tmpProcessTask.getString("name"));
            tmpTask.put("preTaskCode",tmpProcessTask.getLong("preTaskCode"));
            tmpTask.put("preTaskVersion",tmpProcessTask.getInteger("preTaskVersion")+1);
            tmpTask.put("postTaskCode",tmpProcessTask.getLong("postTaskCode"));
            tmpTask.put("postTaskVersion",tmpProcessTask.getInteger("postTaskVersion")+1);
            tmpTask.put("conditionType",tmpProcessTask.getString("conditionType"));
            tmpTask.put("conditionParams",tmpProcessTask.getString("conditionParams"));
            taskRelation.add(tmpTask);
        }
        taskRelationJson = taskRelation.toString();



        JSONArray taskDefinition = JSONUtils.getJSONArray();
        JSONArray taskDefinitionList = processDefinition.getJSONObject("data").getJSONArray("taskDefinitionList");
        for(int i=0; i<taskDefinitionList.size(); i++){
            JSONObject tmptaskDefinition = taskDefinitionList.getJSONObject(i);
            JSONObject tmpTask = (JSONObject)tmptaskDefinition.clone();
            if("DEPENDENT".equals(tmptaskDefinition.getString("taskType"))){
                DependenceModel.TaskParams taskParams = new DependenceModel.TaskParams();
                List<DependencyModel> dependencys = schedulerModel.getDependency();
                List<DependenceModel.DependItem> dependItemList = new ArrayList<>();
                for(DependencyModel dependency:dependencys) {
                    try {
                        JSONObject processDefinitionListPaging = this.schedulerAPI.queryProcessDefinitionListPaging(projectCode, dependency.getDependency());
                        if("success".equals(processDefinitionListPaging.getString("msg"))){
                            JSONArray processDefinitionList = processDefinitionListPaging.getJSONObject("data").getJSONArray("totalList");
                            DependenceModel.DependItem dependItem = new DependenceModel.DependItem();
                            for(int j=0; j<processDefinitionList.size(); j++){
                                JSONObject tmpProcessDefinition = processDefinitionList.getJSONObject(j);
                                if(StringUtils.isNotEmpty(dependency.getDependency()) && dependency.getDependency().equals(tmpProcessDefinition.getString("name"))){
                                    dependItem.setProjectCode(projectCode);
                                    dependItem.setDefinitionCode(tmpProcessDefinition.getLong("code"));
                                    dependItem.setDepTaskCode(0);
                                    dependItem.setCycle(dependency.getPeriod().toString().toLowerCase());
                                    dependItem.setDateValue(dependency.getDateValue());
                                    dependItemList.add(dependItem);
                                    break;
                                }
                            }
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                taskParams.getDependence().getDependTaskList().get(0).setDependItemList(dependItemList);
                tmpTask.put("taskParams",taskParams.toJSONObject());
                tmpTask.put("dependence",taskParams.toJSONObject().toString());
            }else {
                JSONObject taskParams = (JSONObject)tmptaskDefinition.getJSONObject("taskParams").clone();
                taskParams.put("rawScript",schedulerModel.getExecTaskScript());
                tmpTask.put("taskParams",taskParams);
            }
            taskDefinition.add(tmpTask);
        }
        taskDefinitionJson = taskDefinition.toString();


        try {
            JSONObject updateProcessDefinitionResult = this.schedulerAPI.updateProcessDefinition(
                    projectCode,
                    name,
                    processDefinitionCode,
                    locations,
                    taskRelationJson,
                    taskDefinitionJson,
                    releaseState,
                    timeout,
                    globalParams,
                    description,
                    executionType
            );
            return updateProcessDefinitionResult;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除任务
     *
     * @param schedulerModel
     * @return
     * @throws ProjectNotExistsException
     * @throws GenTaskCodeFaildException
     */
    @Override
    public JSONObject deleteTask(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException,TaskCodeNotNullException{

        String name = schedulerModel.getTaskNodeName();
        String project = schedulerModel.getProject();
        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        Long projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        Long processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, name);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }


        try {
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            if(null != scheduleId && scheduleId > 0){
                // 1 下线调度
                boolean scheduleOffline = publishScheduleOffline(projectCode, scheduleId);
                // 2 删除调度
                JSONObject deleteTiming = deleteTiming(schedulerModel);
            }else{}
            // 3 下线任务
            schedulerModel.setReleaseState(0);
            JSONObject offline = release(schedulerModel);
            // 4 删除任务
            JSONObject deleteProcessDefinitionResult = this.schedulerAPI.deleteProcessDefinitionByCode(projectCode, processDefinitionCode);
            return deleteProcessDefinitionResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }



    /**
     * 为工作流创建调度
     *
     * @param schedulerModel
     * @return
     * @throws ProjectNotExistsException
     * @throws GenTaskCodeFaildException
     */
    @Override
    public JSONObject timingTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {
        String name = schedulerModel.getTaskNodeName();
        String project = schedulerModel.getProject();
        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        Long projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        Long processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, name);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        String crontab = "0-0 15-15 0-0 * * ? *";
        String scheduleString = "{\"startTime\":\"2024-08-28 00:00:00\",\"endTime\":\"2124-08-28 00:00:00\",\"crontab\":\"0-0 15-15 0-0 * * ? *\",\"timezoneId\":\"Asia/Shanghai\"}";
        JSONObject schedule = JSONUtils.getJSONObjectFromString(scheduleString);
        try {
            schedule.put("startTime", DateUtils.dateFormat(DateUtils.dateParse(DateUtils.getCurrentDate(),"yyyy-MM-dd"),"yyyy-MM-dd HH:mm:ss"));
            schedule.put("endTime","2100-12-31 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String failureStrategy = "CONTINUE";
        String warningType = "NONE";
        String processInstancePriority = "MEDIUM";
        Integer warningGroupId = 0;
        String workerGroup = "default";
        String tenantCode = "default";
//        Long environmentCode = null;


        try {
            JSONObject scheduleResult = this.schedulerAPI.createSchedule(
                    projectCode,
                    processDefinitionCode,
                    schedule.toString(),
                    warningType,
                    warningGroupId,
                    failureStrategy,
                    workerGroup,
                    tenantCode,
//                    environmentCode,
                    processInstancePriority
            );

            return scheduleResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除调度
     *
     * @param schedulerModel
     * @return
     * @throws ProjectNotExistsException
     * @throws GenTaskCodeFaildException
     */
    @Override
    public JSONObject deleteTiming(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {
        String name = schedulerModel.getTaskNodeName();
        String project = schedulerModel.getProject();
        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        Long projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        Long processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, name);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
        if(null != scheduleId && scheduleId > 0){
            JSONObject deleteScheduleResult = null;
            try {
                deleteScheduleResult = this.schedulerAPI.deleteScheduleById(projectCode, scheduleId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return deleteScheduleResult;
        }else {}
        return null;
    }

    @Override
    public JSONObject queryTaskList(String project, String workFlow) throws ProjectNotExistsException, GenTaskCodeFaildException {
        Long projectCode = getProjectCodeFromProjectName(project);
        JSONObject processList = null;
        try {
            processList = this.schedulerAPI.getProcessListByProjectCode(projectCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return processList;
    }

    @Override
    public JSONObject queryProcessTaskList(String project, String workFlow) throws ProjectNotExistsException, GenTaskCodeFaildException {
        return null;
    }


}
