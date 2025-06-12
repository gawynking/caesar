package com.caesar.scheduler.dolphin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.exception.TaskCodeNotNullException;
import com.caesar.model.DependencyModel;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerInstance;
import com.caesar.scheduler.dolphin.model.BaseModel;
import com.caesar.scheduler.dolphin.model.ShellModel;
import com.caesar.tool.BeanConverterTools;
import com.caesar.util.JSONUtils;
import com.caesar.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 工作流粒度调度管理器
 */
public class DolphinSchedulerWorkflowInstance20250613 extends DolphinSchedulerBaseInstance implements SchedulerInstance{

    private static final Logger logger = Logger.getLogger(DolphinSchedulerWorkflowInstance20250613.class.getName());

    public static DolphinSchedulerProjectInstance projectInstance = new DolphinSchedulerProjectInstance();

    public DolphinSchedulerWorkflowInstance20250613(){
        super();
    }



    @Override
    public JSONObject deployTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException,TaskCodeNotNullException {

        Long projectCode = null;
        Long processDefinitionCode = null;

        String projectStr = schedulerModel.getProject();
        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }

        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        processDefinitionCode = super.getProcessDefinitionCodeFromProcessDefinitionName(projectCode, workFlow);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }


        JSONObject result = null;
        Long taskDefinitionCode = getTaskDefinitionCodeFromTaskDefinitionName(projectCode, processDefinitionCode, schedulerModel.getTaskNodeName());
        if(null == taskDefinitionCode){
            result = this.createTask(schedulerModel);
        }else if(schedulerModel.getIsDelete()){
            result = deleteTask(schedulerModel);
        }else{
            result = updateTask(schedulerModel);
        }

        return result;
    }


    @Override
    public JSONObject createTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {

        Long projectCode = null;
        Long processDefinitionCode = null;
        String taskDefinitionJsonObj = null;
        String upstreamCodes = null;

        String projectStr = schedulerModel.getProject();
        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }

        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        processDefinitionCode = super.getProcessDefinitionCodeFromProcessDefinitionName(projectCode, workFlow);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        Long taskCode = null;
        try {
            taskCode = this.schedulerAPI.genTaskCodeList(projectCode, 1).getJSONArray("data").getLong(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ShellModel shellModel = (ShellModel) BaseModel.getModelMapper().get(ShellModel.class.getName()).getModel();
        shellModel.setCode(taskCode);
        shellModel.setName(schedulerModel.getTaskNodeName());
        shellModel.setFlag(schedulerModel.getReleaseState()==1?"YES":"NO");
        shellModel.getTaskParams().setRawScript(schedulerModel.getExecTaskScript());

        taskDefinitionJsonObj = shellModel.toJSONObject().toString();

        StringBuffer buffer = new StringBuffer();
        List<DependencyModel> dependencyList = Optional.ofNullable(schedulerModel.getDependency()).orElse(new ArrayList<>());
        for(DependencyModel dependency :dependencyList){
            Long taskDefinitionCode = getTaskDefinitionCodeFromTaskDefinitionName(projectCode, processDefinitionCode, dependency.getDependency());
            if(null != taskDefinitionCode) {
                buffer.append(taskDefinitionCode).append(",");
            }
        }
        if(buffer.length()>1) {
            upstreamCodes = buffer.delete(buffer.length() - 1, buffer.length()).toString();
        }

        JSONObject result = null;
        int tmpReleaseState = schedulerModel.getReleaseState();
        try {
            // 1 下线调度
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            publishScheduleOffline(projectCode, scheduleId);
            // 2 下线工作流
            schedulerModel.setReleaseState(0);
            releaseWorkflow(schedulerModel);
            schedulerModel.setReleaseState(tmpReleaseState);
            // 3 创建新任务
            result = this.schedulerAPI.createTaskBindsWorkFlow(projectCode, processDefinitionCode, taskDefinitionJsonObj, upstreamCodes);
            // 4 上线工作流
            schedulerModel.setReleaseState(1);
            releaseWorkflow(schedulerModel);
            schedulerModel.setReleaseState(tmpReleaseState);
            // 5 上线调度
            publishScheduleOnline(projectCode, scheduleId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    @Override
    public JSONObject updateTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException,TaskCodeNotNullException {

        logger.info("开始更新任务: " + schedulerModel.toString());

        SchedulerModel tmpSchedulerModel = BeanConverterTools.convert(schedulerModel, SchedulerModel.class);

        Long projectCode = null;
        Long processDefinitionCode = null;
        String taskDefinitionJsonObj = null;
        String upstreamCodes = null;

        String projectStr = schedulerModel.getProject();
        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }

        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        processDefinitionCode = super.getProcessDefinitionCodeFromProcessDefinitionName(projectCode, workFlow);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        Long taskDefinitionCode = getTaskDefinitionCodeFromTaskDefinitionName(projectCode, processDefinitionCode, schedulerModel.getTaskNodeName());
        if(null == taskDefinitionCode){
            throw new TaskCodeNotNullException("TaskCode不能为空");
        }

        JSONObject taskDefinitionDetail = null;
        try {
            taskDefinitionDetail = this.schedulerAPI.queryTaskDefinitionDetail(projectCode, taskDefinitionCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ShellModel shellModel = (ShellModel) BaseModel.getModelMapper().get(ShellModel.class.getName()).getModel();
        shellModel.setCode(taskDefinitionCode);
        shellModel.setName(schedulerModel.getTaskNodeName());
        shellModel.setFlag(schedulerModel.getReleaseState()==1?"YES":"NO");
        shellModel.getTaskParams().setRawScript(schedulerModel.getExecTaskScript());

        taskDefinitionJsonObj = shellModel.toJSONObject().toString();

        StringBuffer buffer = new StringBuffer();
        List<DependencyModel> dependencyList = schedulerModel.getDependency();
        for(DependencyModel dependency:dependencyList){
            Long tmpTaskCode = getTaskDefinitionCodeFromTaskDefinitionName(projectCode, processDefinitionCode, dependency.getDependency());
            if(null != tmpTaskCode) {
                buffer.append(tmpTaskCode).append(",");
            }
        }
        if(buffer.length()>1) {
            upstreamCodes = buffer.delete(buffer.length() - 1, buffer.length()).toString();
        }

        JSONObject result = null;
        int tmpReleaseState = schedulerModel.getReleaseState();
        try {
            // 1 下线调度
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            publishScheduleOffline(projectCode, scheduleId);
            // 2 下线工作流
            schedulerModel.setReleaseState(0);
            releaseWorkflow(schedulerModel);
            schedulerModel.setReleaseState(tmpReleaseState);
            // 3 更新任务
            result = this.schedulerAPI.updateTaskWithUpstream(projectCode,taskDefinitionCode,taskDefinitionJsonObj,upstreamCodes);
            this.updateTaskForStatus(tmpSchedulerModel);
            // 4 上线工作流
            schedulerModel.setReleaseState(1);
            releaseWorkflow(schedulerModel);
            schedulerModel.setReleaseState(tmpReleaseState);
            // 5 上线调度
            publishScheduleOnline(projectCode, scheduleId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public JSONObject updateTaskForStatus(SchedulerModel schedulerModel) throws ProjectNotExistsException,GenTaskCodeFaildException, TaskCodeNotNullException {

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


        String projectStr = schedulerModel.getProject();
        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }

        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        processDefinitionCode = super.getProcessDefinitionCodeFromProcessDefinitionName(projectCode, workFlow);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        Long taskDefinitionCode = getTaskDefinitionCodeFromTaskDefinitionName(projectCode, processDefinitionCode, schedulerModel.getTaskNodeName());
        if(null == taskDefinitionCode){
            throw new TaskCodeNotNullException("TaskCode不能为空");
        }

        JSONObject processDefinition = null;
        try {
            processDefinition = this.schedulerAPI.queryProcessDefinitionByCode(projectCode, processDefinitionCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        name = processDefinition.getJSONObject("data").getJSONObject("processDefinition").getString("name");
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
            tmpTask.put("preTaskVersion",tmpProcessTask.getInteger("preTaskVersion"));
            tmpTask.put("postTaskCode",tmpProcessTask.getLong("postTaskCode"));
            tmpTask.put("postTaskVersion",tmpProcessTask.getInteger("postTaskVersion"));
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
            tmpTask.put("id",tmptaskDefinition.getString("id"));
            tmpTask.put("code",tmptaskDefinition.getString("code"));
            tmpTask.put("delayTime",tmptaskDefinition.getString("delayTime"));
            tmpTask.put("description",tmptaskDefinition.getString("description"));
            tmpTask.put("environmentCode",tmptaskDefinition.getString("environmentCode"));
            tmpTask.put("isCache",tmptaskDefinition.getString("isCache"));
            tmpTask.put("name",tmptaskDefinition.getString("name"));
            tmpTask.put("taskGroupId",tmptaskDefinition.getString("taskGroupId"));
            tmpTask.put("taskGroupPriority",tmptaskDefinition.getString("taskGroupPriority"));
            tmpTask.put("taskPriority",tmptaskDefinition.getString("taskPriority"));
            tmpTask.put("taskType",tmptaskDefinition.getString("taskType"));
            tmpTask.put("timeout",tmptaskDefinition.getString("timeout"));
            tmpTask.put("timeoutFlag",tmptaskDefinition.getString("timeoutFlag"));
            tmpTask.put("timeoutNotifyStrategy",tmptaskDefinition.getString("timeoutNotifyStrategy"));
            tmpTask.put("workerGroup",tmptaskDefinition.getString("workerGroup"));
            tmpTask.put("cpuQuota",tmptaskDefinition.getString("cpuQuota"));
            tmpTask.put("memoryMax",tmptaskDefinition.getString("memoryMax"));
            tmpTask.put("taskExecuteType",tmptaskDefinition.getString("taskExecuteType"));
            tmpTask.put("version",tmptaskDefinition.getString("version"));

            tmpTask.put("failRetryInterval",tmptaskDefinition.getInteger("failRetryInterval")); // retryInterval
            tmpTask.put("failRetryTimes",tmptaskDefinition.getInteger("failRetryTimes")); // retryTimes
            tmpTask.put("flag",tmptaskDefinition.getString("flag")); // ReleaseState
            tmpTask.put("taskParams",tmptaskDefinition.getString("taskParams")); // execTaskScript

            if(tmptaskDefinition.getString("code").equals(taskDefinitionCode)){
//                tmpTask.put("failRetryInterval",Optional.ofNullable(schedulerModel.getRetryInterval()).orElse(tmptaskDefinition.getInteger("failRetryInterval"))); // retryInterval
//                tmpTask.put("failRetryTimes",Optional.ofNullable(schedulerModel.getRetryTimes()).orElse(tmptaskDefinition.getInteger("failRetryTimes"))); // retryTimes
//                tmpTask.put("flag",schedulerModel.getReleaseState()==1?"YES":"NO"); // ReleaseState
//                JSONObject taskParams = tmptaskDefinition.getJSONObject("taskParams");
//                taskParams.put("rawScript",Optional.ofNullable(schedulerModel.getExecTaskScript()).orElse(taskParams.getString("rawScript")));
//                tmpTask.put("taskParams",taskParams.toString()); // execTaskScript
                tmpTask.put("flag",schedulerModel.getReleaseState()==1?"YES":"NO"); // ReleaseState
                logger.info("开始切换任务上下线.");
                logger.info("本次变更任务状态: " + (schedulerModel.getReleaseState()==1?"YES":"NO"));
                logger.info("本次任务状态: " + tmptaskDefinition.getString("flag"));
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


    @Override
    public JSONObject deleteTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException,TaskCodeNotNullException {

        String projectStr = schedulerModel.getProject();
        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }

        if(StringUtils.isEmpty(project)){
            throw new ProjectNotExistsException("Project不能为空");
        }

        Long projectCode = getProjectCodeFromProjectName(project);
        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        Long processDefinitionCode = super.getProcessDefinitionCodeFromProcessDefinitionName(projectCode, workFlow);
        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        Long taskDefinitionCode = getTaskDefinitionCodeFromTaskDefinitionName(projectCode, processDefinitionCode, schedulerModel.getTaskNodeName());
        if(null == taskDefinitionCode){
            throw new TaskCodeNotNullException("TaskCode不能为空");
        }

        JSONObject result = null;
        int tmpReleaseState = schedulerModel.getReleaseState();
        try {
            // 1 下线调度
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            publishScheduleOffline(projectCode, scheduleId);
            // 2 下线工作流
            schedulerModel.setReleaseState(0);
            releaseWorkflow(schedulerModel);
            schedulerModel.setReleaseState(tmpReleaseState);
            // 3 删除任务
            result = this.schedulerAPI.deleteTaskDefinitionByCode(projectCode, taskDefinitionCode);
            // 4 上线工作流
            schedulerModel.setReleaseState(1);
            releaseWorkflow(schedulerModel);
            schedulerModel.setReleaseState(tmpReleaseState);
            // 5 上线调度
            publishScheduleOnline(projectCode, scheduleId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public JSONObject releaseWorkflow(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {
        String projectStr = schedulerModel.getProject();
        String nameStr = schedulerModel.getTaskNodeName();

        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }
        schedulerModel.setProject(project);
        schedulerModel.setTaskNodeName(workFlow);
        JSONObject result = super.release(schedulerModel);
        schedulerModel.setProject(projectStr);
        schedulerModel.setTaskNodeName(nameStr);
        return result;
    }

    @Override
    public JSONObject timingTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {
        String projectStr = schedulerModel.getProject();
        String nameStr = schedulerModel.getTaskNodeName();
        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }
        schedulerModel.setProject(project);
        schedulerModel.setTaskNodeName(workFlow);
        JSONObject result = projectInstance.timingTask(schedulerModel);
        schedulerModel.setProject(projectStr);
        schedulerModel.setTaskNodeName(nameStr);
        return result;
    }

    @Override
    public JSONObject deleteTiming(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {
        String projectStr = schedulerModel.getProject();
        String nameStr = schedulerModel.getTaskNodeName();
        String project = null;
        String workFlow = null;
        if(projectStr.contains("___")){
            String[] projectArray = projectStr.split("___");
            project = projectArray[0];
            workFlow = projectArray[1];
        }
        schedulerModel.setProject(project);
        schedulerModel.setTaskNodeName(workFlow);
        JSONObject result = projectInstance.deleteTiming(schedulerModel);
        schedulerModel.setProject(projectStr);
        schedulerModel.setTaskNodeName(nameStr);
        return result;
    }


    @Override
    public JSONObject queryTaskList(String project, String workFlow) throws ProjectNotExistsException, GenTaskCodeFaildException {

        Long projectCode = getProjectCodeFromProjectName(project);
        Long processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, workFlow);
        JSONObject processDefinitionList = null;
        try {
            processDefinitionList = this.schedulerAPI.getTaskListByProcessDefinitionCode(projectCode, processDefinitionCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return processDefinitionList;
    }


    @Override
    public JSONObject queryProcessTaskList(String project, String workFlow) throws ProjectNotExistsException, GenTaskCodeFaildException {

        Long projectCode = getProjectCodeFromProjectName(project);
        Long processDefinitionCode = getProcessDefinitionCodeFromProcessDefinitionName(projectCode, workFlow);

        if(null == projectCode){
            throw new ProjectNotExistsException("DolphinScheduler没有定义指定Project");
        }

        if(null == processDefinitionCode){
            throw new GenTaskCodeFaildException("更新的processDefinitionCode不存在");
        }

        JSONObject processDefinitionList = null;
        try {
            processDefinitionList = this.schedulerAPI.queryProcessDefinitionByCode(projectCode, processDefinitionCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return processDefinitionList;

    }

}
