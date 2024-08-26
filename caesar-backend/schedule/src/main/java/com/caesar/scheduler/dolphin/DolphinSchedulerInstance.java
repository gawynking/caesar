package com.caesar.scheduler.dolphin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.model.DependencyModel;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerInstance;
import com.caesar.scheduler.dolphin.model.*;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPI;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPIFactory;
import com.caesar.util.JSONUtils;
import com.caesar.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 接口文档地址：http://127.0.0.1:12345/dolphinscheduler/swagger-ui/index.html?language=zh_CN&lang=cn&urls.primaryName=v2
 */
public class DolphinSchedulerInstance implements SchedulerInstance {

    private static final Logger LOGGER = Logger.getLogger(DolphinSchedulerInstance.class.getName());

    DolphinSchedulerAPI schedulerAPI;

    public DolphinSchedulerInstance(){
        this.schedulerAPI = DolphinSchedulerAPIFactory.getDolphinSchedulerAPI();
    }




    @Override
    public void deployTask(SchedulerModel schedulerModel) {
    }


    /* demo:
        taskDefinitionJson: [{"code":14583308516544,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"0","flag":"YES","isCache":"NO","name":"gawyn-shell","taskParams":{"localParams":[],"rawScript":"echo gawyn","resourceList":[]},"taskPriority":"MEDIUM","taskType":"SHELL","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH"},{"code":14583311968576,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"0","flag":"YES","isCache":"NO","name":"gawyn-dep","taskParams":{"localParams":[],"resourceList":[],"dependence":{"checkInterval":10,"failurePolicy":"DEPENDENT_FAILURE_FAILURE","relation":"AND","dependTaskList":[{"relation":"AND","dependItemList":[{"projectCode":14576322429504,"definitionCode":14578042471232,"depTaskCode":0,"cycle":"month","dateValue":"thisMonth","state":null}]}]}},"taskPriority":"MEDIUM","taskType":"DEPENDENT","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH"}]
        taskRelationJson: [{"name":"","preTaskCode":0,"preTaskVersion":0,"postTaskCode":14583311968576,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}},{"name":"","preTaskCode":14583311968576,"preTaskVersion":0,"postTaskCode":14583308516544,"postTaskVersion":0,"conditionType":"NONE","conditionParams":{}}]
        locations: [{"taskCode":14583308516544,"x":289,"y":46},{"taskCode":14583311968576,"x":66,"y":118}]
        name: gawyn-001
        executionType: PARALLEL
        description:
        globalParams: [{"prop":"dd","value":"1","direct":"IN","type":"VARCHAR"},{"prop":"ff","value":"2","direct":"IN","type":"VARCHAR"}]
        timeout: 0
     */
    @Override
    public String createTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {

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
        String globalParams = null;
        int timeout = 0;
        String locations = null;

        String taskRelationJson = null;
        String taskDefinitionJson = null;


//        try {
//            JSONObject projectListObject = this.schedulerAPI.queryAllProjectList();
//            if(!"success".equals(projectListObject.getString("msg"))){
//                LOGGER.warning("查询项目列表接口失败");
//                return null;
//            }
//            JSONArray projectList = projectListObject.getJSONArray("data");
//            for (int i = 0; i < projectList.size(); i++) {
//                JSONObject tmpProject = projectList.getJSONObject(i);
//                if (project.equals(tmpProject.getString("name"))) {
//                    projectCode = tmpProject.getLong("code");
//                    break;
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

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
        globalParams = schedulerModel.getGlobalParams();
        timeout = schedulerModel.getTimeout();

//        try {
//            JSONObject processDefinitionListPaging = this.schedulerAPI.queryProcessDefinitionListPaging(projectCode, name);
//            if("success".equals(processDefinitionListPaging.getString("msg"))){
//                JSONArray processDefinitionList = processDefinitionListPaging.getJSONObject("data").getJSONArray("totalList");
//                for(int i=0; i<processDefinitionList.size(); i++){
//                    JSONObject processDefinition = processDefinitionList.getJSONObject(i);
//                    if(name.equals(processDefinition.getString("name"))){
//                        LOGGER.warning("任务名称已存在，不能重复创建");
//                        return null;
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
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
        taskDefinitionTask.getTaskParams().setRawScript(schedulerModel.getTaskScript());

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


        LOGGER.info("本次执行任务 \n" + taskDefinitionJson);


        try{
            JSONObject processDefinition = this.schedulerAPI.createProcessDefinition(
                    projectCode,
                    name,
                    locations,
                    taskRelationJson,
                    taskDefinitionJson
            );
            return processDefinition.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }





    /* demo:
        - GET - http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14622363103936
        - PUT - http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition/14622363103936
        taskDefinitionJson: [{"code":14624848670912,"delayTime":"0","description":"","environmentCode":-1,"failRetryInterval":"1","failRetryTimes":"3","flag":"YES","isCache":"NO","name":"gawyn-test15","taskGroupId":null,"taskGroupPriority":null,"taskParams":{"localParams":[],"rawScript":"echo test15-update","resourceList":[]},"taskPriority":"MEDIUM","taskType":"SHELL","timeout":0,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":"","workerGroup":"default","cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH","version":1,"id":47},{"id":48,"code":14624848671936,"name":"gawyn-test15.scheduler","version":1,"description":"","projectCode":14576322429504,"userId":1,"taskType":"DEPENDENT","taskParams":{"dependence":{"checkInterval":10,"dependTaskList":[{"dependItemList":[{"dateValue":"today","depTaskCode":0,"projectCode":14576322429504,"definitionCode":14620937083584,"cycle":"day"}],"relation":"AND"}],"failurePolicy":"DEPENDENT_FAILURE_FAILURE","relation":"AND"},"localParams":[],"resourceList":[]},"taskParamList":[],"taskParamMap":null,"flag":"YES","isCache":"NO","taskPriority":"MEDIUM","userName":null,"projectName":null,"workerGroup":"default","environmentCode":-1,"failRetryTimes":3,"failRetryInterval":1,"timeoutFlag":"CLOSE","timeoutNotifyStrategy":null,"timeout":0,"delayTime":0,"resourceIds":null,"createTime":"2024-08-27 01:48:00","updateTime":"2024-08-27 01:48:00","modifyBy":null,"taskGroupId":0,"taskGroupPriority":0,"cpuQuota":-1,"memoryMax":-1,"taskExecuteType":"BATCH","operator":1,"operateTime":"2024-08-27 01:48:00","dependence":"{\"checkInterval\":10,\"dependTaskList\":[{\"dependItemList\":[{\"dateValue\":\"today\",\"depTaskCode\":0,\"projectCode\":14576322429504,\"definitionCode\":14620937083584,\"cycle\":\"day\"}],\"relation\":\"AND\"}],\"failurePolicy\":\"DEPENDENT_FAILURE_FAILURE\",\"relation\":\"AND\"}"}]
        taskRelationJson: [{"name":"","preTaskCode":0,"preTaskVersion":0,"postTaskCode":14624848671936,"postTaskVersion":1,"conditionType":"NONE","conditionParams":{}},{"name":"","preTaskCode":14624848671936,"preTaskVersion":1,"postTaskCode":14624848670912,"postTaskVersion":1,"conditionType":"NONE","conditionParams":{}}]
        locations: [{"taskCode":14624848670912,"x":94,"y":16},{"taskCode":14624848671936,"x":94,"y":98}]
        name: gawyn-test15
        executionType: PARALLEL
        description:
        globalParams: []
        timeout: 0
        releaseState: OFFLINE
     */
    @Override
    public String updateTask(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException{

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

        JSONObject processDefinition = null;
        try {
            processDefinition = this.schedulerAPI.queryProcessDefinitionByCode(projectCode, processDefinitionCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        String executionType = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("executionType");
        String description = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("description");
        String globalParams = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("globalParams");
        int timeout = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getInteger("timeout");
        String releaseState = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("releaseState");
        String locations = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("locations");

        JSONArray taskRelation = JSONUtils.getJSONArray();
        JSONArray processTaskRelationList = processDefinition.getJSONObject("data").getJSONArray("processTaskRelationList");
        for(int i=0; i<processTaskRelationList.size(); i++){
            JSONObject tmpTask = JSONUtils.getJSONObject();
            JSONObject tmpProcessTask = processTaskRelationList.getJSONObject(i);
            tmpTask.put("name",tmpProcessTask.getString("name"));
            tmpTask.put("preTaskCode",tmpProcessTask.getLong("preTaskCode"));
            tmpTask.put("preTaskVersion",tmpProcessTask.getInteger("preTaskVersion"));
            tmpTask.put("postTaskCode",tmpProcessTask.getLong("postTaskCode"));
            tmpTask.put("postTaskVersion",tmpProcessTask.getInteger("postTaskVersion"));
            tmpTask.put("conditionType",tmpProcessTask.getString("conditionType"));
            tmpTask.put("conditionParams",tmpProcessTask.getString("conditionParams"));
            taskRelation.add(tmpTask);
        }
        String taskRelationJson = taskRelation.toString();



        JSONArray taskDefinition = JSONUtils.getJSONArray();
        JSONArray taskDefinitionList = processDefinition.getJSONObject("data").getJSONArray("taskDefinitionList");
        for(int i=0; i<taskDefinitionList.size(); i++){
            JSONObject tmpTask = JSONUtils.getJSONObject();
            JSONObject tmptaskDefinition = taskDefinitionList.getJSONObject(i);
            if("DEPENDENT".equals(tmptaskDefinition.getString("taskType"))){
                tmpTask.put("code",tmptaskDefinition.getLong("code"));
                tmpTask.put("delayTime",tmptaskDefinition.getInteger("delayTime"));
                tmpTask.put("description",tmptaskDefinition.getString("description"));
                tmpTask.put("environmentCode",tmptaskDefinition.getInteger("environmentCode"));
                tmpTask.put("failRetryInterval",tmptaskDefinition.getString("failRetryInterval"));
                tmpTask.put("failRetryTimes",tmptaskDefinition.getString("failRetryTimes"));
                tmpTask.put("flag",tmptaskDefinition.getString("flag"));
                tmpTask.put("isCache",tmptaskDefinition.getString("isCache"));
                tmpTask.put("name",tmptaskDefinition.getString("name"));
                tmpTask.put("taskGroupId",tmptaskDefinition.getInteger("taskGroupId"));
                tmpTask.put("taskGroupPriority",tmptaskDefinition.getInteger("taskGroupPriority"));
                tmpTask.put("taskPriority",tmptaskDefinition.getString("taskPriority"));
                tmpTask.put("taskType",tmptaskDefinition.getString("taskType"));
                tmpTask.put("timeout",tmptaskDefinition.getInteger("timeout"));
                tmpTask.put("timeoutFlag",tmptaskDefinition.getString("timeoutFlag"));
                tmpTask.put("timeoutNotifyStrategy",tmptaskDefinition.getString("timeoutNotifyStrategy"));
                tmpTask.put("workerGroup",tmptaskDefinition.getString("workerGroup"));
                tmpTask.put("cpuQuota",tmptaskDefinition.getInteger("cpuQuota"));
                tmpTask.put("memoryMax",tmptaskDefinition.getInteger("memoryMax"));
                tmpTask.put("taskExecuteType",tmptaskDefinition.getString("taskExecuteType"));
                tmpTask.put("version",tmptaskDefinition.getInteger("version")+1);
//                tmpTask.put("id",tmptaskDefinition.getInteger("id"));

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
            }else {
                tmpTask.put("code",tmptaskDefinition.getLong("code"));
                tmpTask.put("delayTime",tmptaskDefinition.getInteger("delayTime"));
                tmpTask.put("description",tmptaskDefinition.getString("description"));
                tmpTask.put("environmentCode",tmptaskDefinition.getInteger("environmentCode"));
                tmpTask.put("failRetryInterval",tmptaskDefinition.getString("failRetryInterval"));
                tmpTask.put("failRetryTimes",tmptaskDefinition.getString("failRetryTimes"));
                tmpTask.put("flag",tmptaskDefinition.getString("flag"));
                tmpTask.put("isCache",tmptaskDefinition.getString("isCache"));
                tmpTask.put("name",tmptaskDefinition.getString("name"));
                tmpTask.put("taskGroupId",tmptaskDefinition.getString("taskGroupId"));
                tmpTask.put("taskGroupPriority",tmptaskDefinition.getString("taskGroupPriority"));
                tmpTask.put("taskPriority",tmptaskDefinition.getString("taskPriority"));
                tmpTask.put("taskType",tmptaskDefinition.getString("taskType"));
                tmpTask.put("timeout",tmptaskDefinition.getInteger("timeout"));
                tmpTask.put("timeoutFlag",tmptaskDefinition.getString("timeoutFlag"));
                tmpTask.put("timeoutNotifyStrategy",tmptaskDefinition.getString("timeoutNotifyStrategy"));
                tmpTask.put("workerGroup",tmptaskDefinition.getString("workerGroup"));
                tmpTask.put("cpuQuota",tmptaskDefinition.getInteger("cpuQuota"));
                tmpTask.put("memoryMax",tmptaskDefinition.getInteger("memoryMax"));
                tmpTask.put("taskExecuteType",tmptaskDefinition.getString("taskExecuteType"));
                tmpTask.put("version",tmptaskDefinition.getInteger("version")+1);
//                tmpTask.put("id",tmptaskDefinition.getLong("id"));

                JSONObject taskParams = JSONUtils.getJSONObject();
                taskParams.put("localParams",tmptaskDefinition.getJSONObject("taskParams").getString("localParams"));
                taskParams.put("resourceList",tmptaskDefinition.getJSONObject("taskParams").getString("resourceList"));
                taskParams.put("rawScript",schedulerModel.getTaskScript());

                tmpTask.put("taskParams",taskParams);
            }
            taskDefinition.add(tmpTask);
        }
        String taskDefinitionJson = taskDefinition.toString();


        System.out.println("--------------------------------------------");
        System.out.println(projectCode);
        System.out.println(name);
        System.out.println(processDefinitionCode);
        System.out.println(locations);
        System.out.println(taskRelationJson);
        System.out.println(taskDefinitionJson);
        System.out.println("--------------------------------------------");

        try {
            JSONObject updateProcessDefinitionResult = this.schedulerAPI.updateProcessDefinition(
                    projectCode,
                    name,
                    processDefinitionCode,
                    locations,
                    taskRelationJson,
                    taskDefinitionJson
            );
            return updateProcessDefinitionResult.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }





    @Override
    public void deleteTask(String taskId) {

    }

    @Override
    public String queryTask(String taskId) {

        return "Task details for " + taskId;
    }

    @Override
    public void pauseTask(String taskId) {

    }

    @Override
    public void resumeTask(String taskId) {

    }

    @Override
    public String queryTaskLogs(String taskId) {

        return "Task logs for " + taskId;
    }

    @Override
    public void addTaskDependency(String taskId, SchedulerModel schedulerModel) {

    }



    public Long getProjectCodeFromProjectName(String project){
        try {
            JSONObject projectListObject = this.schedulerAPI.queryAllProjectList();
            if(!"success".equals(projectListObject.getString("msg"))){
                LOGGER.warning("查询项目列表接口失败");
                return null;
            }
            JSONArray projectList = projectListObject.getJSONArray("data");
            for (int i = 0; i < projectList.size(); i++) {
                JSONObject tmpProject = projectList.getJSONObject(i);
                if (project.equals(tmpProject.getString("name"))) {
                    return tmpProject.getLong("code");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 根据工作流名称查询工作流code
     *
     * @param name
     * @return
     */
    public Long getProcessDefinitionCodeFromProcessDefinitionName(Long projectCode, String name){
        try {
            JSONObject processDefinitionListPaging = this.schedulerAPI.queryProcessDefinitionListPaging(projectCode, name);
            if("success".equals(processDefinitionListPaging.getString("msg"))){
                JSONArray processDefinitionList = processDefinitionListPaging.getJSONObject("data").getJSONArray("totalList");
                for(int i=0; i<processDefinitionList.size(); i++){
                    JSONObject processDefinition = processDefinitionList.getJSONObject(i);
                    if(name.equals(processDefinition.getString("name"))){
                        return processDefinition.getLong("code");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
