package com.caesar.scheduler.dolphin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caesar.enums.ReleaseState;
import com.caesar.exception.GenTaskCodeFaildException;
import com.caesar.exception.ProjectNotExistsException;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPI;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPIFactory;
import com.caesar.util.StringUtils;

import java.io.IOException;
import java.util.logging.Logger;


public abstract class DolphinSchedulerBaseInstance {


    private static final Logger logger = Logger.getLogger(DolphinSchedulerBaseInstance.class.getName());

    DolphinSchedulerAPI schedulerAPI;

    public DolphinSchedulerBaseInstance(){
        this.schedulerAPI = DolphinSchedulerAPIFactory.getDolphinSchedulerAPI();
    }


    /**
     * 工作流发布
     *
     * @param schedulerModel
     * @return
     * @throws ProjectNotExistsException
     * @throws GenTaskCodeFaildException
     */
    public JSONObject release(SchedulerModel schedulerModel) throws ProjectNotExistsException, GenTaskCodeFaildException {

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
            String releaseState = ReleaseState.getEnum(schedulerModel.getReleaseState()).getDescp().toUpperCase();
            JSONObject releaseResult = this.schedulerAPI.releaseProcessDefinition(projectCode, processDefinitionCode,releaseState);
            return releaseResult;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 调度上线
     *
     * @param projectCode
     * @param scheduleId
     * @return
     */
    public boolean publishScheduleOnline(long projectCode, int scheduleId){
        try {
            JSONObject publishScheduleOnline = this.schedulerAPI.publishScheduleOnline(projectCode, scheduleId);
            if("success".equals(publishScheduleOnline.getString("msg"))){
                return true;
            }else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 调度下线
     *
     * @param projectCode
     * @param scheduleId
     * @return
     */
    public boolean publishScheduleOffline(long projectCode, int scheduleId){
        try {
            JSONObject publishScheduleOnline = this.schedulerAPI.offlineSchedule(projectCode, scheduleId);
            if("success".equals(publishScheduleOnline.getString("msg"))){
                return true;
            }else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 根据工作流ID查询调度ID
     *
     * @param projectCode
     * @param processDefinitionCode
     * @return
     */
    public Integer getScheduleIdFromPorcessDefinitionCode(Long projectCode,Long processDefinitionCode){
        try {
            JSONObject scheduleListPaging = this.schedulerAPI.queryScheduleListPaging(projectCode, processDefinitionCode);
            JSONArray scheduleList = null;
            if(StringUtils.isNotEmpty(scheduleListPaging.getString("data")) && StringUtils.isNotEmpty(scheduleListPaging.getJSONObject("data").getString("totalList"))) {
                scheduleList = scheduleListPaging.getJSONObject("data").getJSONArray("totalList");
            }
            if(null != scheduleList && scheduleList.size()>0){
                return scheduleList.getJSONObject(0).getInteger("id");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据projectName获取projectCode
     *
     * @param project
     * @return
     */
    public Long getProjectCodeFromProjectName(String project){
        try {
            JSONObject projectListObject = this.schedulerAPI.queryAllProjectList();
            if(!"success".equals(projectListObject.getString("msg"))){
                logger.warning("查询项目列表接口失败");
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


    /**
     * 根据taskName查询返回taskCode
     *
     * @param projectCode
     * @param processDefinitionCode
     * @param taskName
     * @return
     */
    public Long getTaskDefinitionCodeFromTaskDefinitionName(Long projectCode, Long processDefinitionCode, String taskName){
        if(null == taskName){
            return null;
        }
        JSONObject processDefinitionJson = null;
        try {
            processDefinitionJson = this.schedulerAPI.queryProcessDefinitionByCode(projectCode, processDefinitionCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray taskDefinitionList = processDefinitionJson.getJSONObject("data").getJSONArray("taskDefinitionList");
        for(int i=0; i<taskDefinitionList.size(); i++){
            JSONObject tmpTaskDefinition = taskDefinitionList.getJSONObject(i);
            if(taskName.equals(tmpTaskDefinition.getString("name"))){
                return tmpTaskDefinition.getLong("code");
            }
        }
        return null;
    }


}
