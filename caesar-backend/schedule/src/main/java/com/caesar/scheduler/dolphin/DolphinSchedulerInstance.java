package com.caesar.scheduler.dolphin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerInstance;
import com.caesar.scheduler.dolphin.model.*;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPI;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPIFactory;
import com.caesar.util.JSONUtils;
import com.caesar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口文档地址：http://127.0.0.1:12345/dolphinscheduler/swagger-ui/index.html?language=zh_CN&lang=cn&urls.primaryName=v2
 */
public class DolphinSchedulerInstance implements SchedulerInstance {

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
    public void createTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {

        String project = schedulerModel.getProject();
        if(StringUtils.isEmpty(project)){
             throw new ProjectNotExistsException("");
        }

        long projectCode = 0l;
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

        try {
            JSONObject projectListObject = this.schedulerAPI.queryAllProjectList();
            JSONArray projectList = projectListObject.getJSONArray("data");
            for (int i = 0; i < projectList.size(); i++) {
                JSONObject tmpProject = projectList.getJSONObject(i);
                if (project.equals(tmpProject.getString("name"))) {
                    projectCode = tmpProject.getLong("code");
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(projectCode == 0l){
            throw new ProjectNotExistsException("");
        }
        try {
            taskCode = this.schedulerAPI.genTaskCodeList(projectCode, 1).getJSONArray("data").getLong(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(taskCode == 0l){
            throw new GenTaskCodeFaildException();
        }
        try {
            dependenceCode = this.schedulerAPI.genTaskCodeList(projectCode, 1).getJSONArray("data").getLong(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(dependenceCode == 0l){
            throw new GenTaskCodeFaildException();
        }

        name = schedulerModel.getTaskNodeName();
        executionType = schedulerModel.getExecutionType();
        description = schedulerModel.getDescription();
        globalParams = schedulerModel.getGlobalParams();
        timeout = schedulerModel.getTimeout();

        JSONArray locationList = JSONUtils.getJSONArray();
        Location locationTask = (Location)BaseModel.getModelMapper().get(Location.class.getName()).getModel();
        Location locationDepend = (Location)BaseModel.getModelMapper().get(Location.class.getName()).getModel();
        locationTask.setTaskCode(taskCode);
        locationDepend.setTaskCode(dependenceCode);
        locationList.add(locationTask.toJSONObject());
        locationList.add(locationDepend.toJSONObject());
        locations = locationList.toString();

        JSONArray taskRelationList = JSONUtils.getJSONArray();
        TaskRelation taskRelationTask = (TaskRelation)BaseModel.getModelMapper().get(TaskRelation.class.getName()).getModel();
        TaskRelation taskRelationDepend = (TaskRelation)BaseModel.getModelMapper().get(TaskRelation.class.getName()).getModel();
        taskRelationList.add(taskRelationTask);
        taskRelationList.add(taskRelationDepend);
        taskRelationTask.setPreTaskCode(dependenceCode);
        taskRelationTask.setPostTaskCode(taskCode);
        taskRelationDepend.setPostTaskCode(dependenceCode);
        taskRelationJson = taskRelationList.toString();

//        taskDefinitionJson
        JSONArray taskDefinitionList = JSONUtils.getJSONArray();
        ShellModel taskDefinitionTask = (ShellModel)BaseModel.getModelMapper().get(ShellModel.class.getName()).getModel();
        DependenceModel taskDefinitionDepend = (DependenceModel)BaseModel.getModelMapper().get(DependenceModel.class.getName()).getModel();
        taskDefinitionList.add(taskDefinitionTask);
        taskDefinitionList.add(taskDefinitionDepend);
        taskDefinitionTask.setCode(taskCode);
        taskDefinitionTask.setName(schedulerModel.getTaskNodeName());
        taskDefinitionTask.getTaskParams().setRawScript(schedulerModel.getTaskScript());

        taskDefinitionDepend.setCode(dependenceCode);
        taskDefinitionDepend.setName(schedulerModel.getTaskNodeName()+".scheduler");

        List<String> dependencys = schedulerModel.getDependency();
        List<DependenceModel.DependItem> dependItemList = new ArrayList<>();
        for(String dependency:dependencys) {
            DependenceModel.DependItem dependItem = new DependenceModel.DependItem();
            dependItemList.add(dependItem);
        }
        taskDefinitionDepend.getTaskParams().getDependence().getDependTaskList().get(0).setDependItemList(dependItemList);
        taskDefinitionJson = taskDefinitionList.toString();

        try{
            JSONObject processDefinition = this.schedulerAPI.createProcessDefinition(
                    projectCode,
                    name,
                    locations,
                    taskRelationJson,
                    taskDefinitionJson
            );
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void updateTask(String taskId, SchedulerModel schedulerModel) {

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

}
