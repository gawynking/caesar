package com.caesar.scheduler.dolphin;

import com.alibaba.fastjson.JSONObject;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.exception.TaskCodeNotNullException;
import com.caesar.model.DependencyModel;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerInstance;
import com.caesar.scheduler.dolphin.model.BaseModel;
import com.caesar.scheduler.dolphin.model.ShellModel;
import com.caesar.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * 工作流粒度调度管理器
 */
public class DolphinSchedulerWorkflowInstance extends DolphinSchedulerBaseInstance implements SchedulerInstance{

    private static final Logger LOGGER = Logger.getLogger(DolphinSchedulerWorkflowInstance.class.getName());

    public static DolphinSchedulerProjectInstance projectInstance = new DolphinSchedulerProjectInstance();

    public DolphinSchedulerWorkflowInstance(){
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
        }else{
            if(schedulerModel.getIsDelete()){
                result = deleteTask(schedulerModel);
            }else{
                result = updateTask(schedulerModel);
            }
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
        shellModel.getTaskParams().setRawScript(schedulerModel.getExecTaskScript());

        taskDefinitionJsonObj = shellModel.toJSONObject().toString();

        StringBuffer buffer = new StringBuffer();
        List<DependencyModel> dependencyList = schedulerModel.getDependency();
        for(DependencyModel dependency:dependencyList){
            Long taskDefinitionCode = getTaskDefinitionCodeFromTaskDefinitionName(projectCode, processDefinitionCode, dependency.getDependency());
            if(null != taskDefinitionCode) {
                buffer.append(taskDefinitionCode).append(",");
            }
        }
        if(buffer.length()>1) {
            upstreamCodes = buffer.delete(buffer.length() - 1, buffer.length()).toString();
        }

        JSONObject result = null;
        try {
            // 1 下线调度
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            publishScheduleOffline(projectCode, scheduleId);
            // 2 下线工作流
            schedulerModel.setReleaseState(0);
            release(schedulerModel);
            // 3 创建新任务
            result = this.schedulerAPI.createTaskBindsWorkFlow(projectCode, processDefinitionCode, taskDefinitionJsonObj, upstreamCodes);
            // 4 上线工作流
            schedulerModel.setReleaseState(1);
            release(schedulerModel);
            // 5 上线调度
            publishScheduleOnline(projectCode, scheduleId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    @Override
    public JSONObject updateTask(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException,TaskCodeNotNullException {

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
        try {
            // 1 下线调度
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            publishScheduleOffline(projectCode, scheduleId);
            // 2 下线工作流
            schedulerModel.setReleaseState(0);
            release(schedulerModel);
            // 3 更新任务
            result = this.schedulerAPI.updateTaskWithUpstream(projectCode,taskDefinitionCode,taskDefinitionJsonObj,upstreamCodes);
            // 4 上线工作流
            schedulerModel.setReleaseState(1);
            release(schedulerModel);
            // 5 上线调度
            publishScheduleOnline(projectCode, scheduleId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
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
        try {
            // 1 下线调度
            Integer scheduleId = getScheduleIdFromPorcessDefinitionCode(projectCode, processDefinitionCode);
            publishScheduleOffline(projectCode, scheduleId);
            // 2 下线工作流
            schedulerModel.setReleaseState(0);
            release(schedulerModel);
            // 3 删除任务
            result = this.schedulerAPI.deleteTaskDefinitionByCode(projectCode, taskDefinitionCode);
            // 4 上线工作流
            schedulerModel.setReleaseState(1);
            release(schedulerModel);
            // 5 上线调度
            publishScheduleOnline(projectCode, scheduleId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    @Override
    public JSONObject release(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {
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
