package com.caesar;

import com.alibaba.fastjson.JSONObject;
import com.caesar.enums.SchedulingPeriod;
import com.caesar.model.DependencyModel;
import com.caesar.model.ScheduleResponse;
import com.caesar.model.SchedulerModel;
import com.caesar.scheduler.SchedulerFacade;
import com.caesar.scheduler.dolphin.DolphinSchedulerProjectInstance;
import com.caesar.scheduler.dolphin.DolphinSchedulerWorkflowInstance;
import com.caesar.scheduler.dolphin.model.*;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPI;
import com.caesar.scheduler.dolphin.process.DolphinSchedulerAPI320;
import com.caesar.util.SchedulerUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DolphinSchedulerApiTest {


    String baseUrl = "http://localhost:12345/dolphinscheduler";
    String token = "86c184558e3e773655504a39e01a1eb6";
    long projectCode = 14576322429504l;
    DolphinSchedulerAPI schedulerAPI = new DolphinSchedulerAPI320(baseUrl,token);


    @Test
    public void test01() throws Exception{

        JSONObject jsonObject = schedulerAPI.genTaskCodeList(projectCode, 1);
        System.out.println(jsonObject);

    }

    @Test
    public void test02() throws Exception{
        JSONObject jsonObject = schedulerAPI.queryAllProjectList();
        System.out.println(jsonObject);
    }


//    long projectCode,
//    String name,
//    String locations,
//    String taskRelationJson,
//    String taskDefinitionJson

    @Test
    public void test03() throws Exception{
        JSONObject jsonObject = schedulerAPI.createProcessDefinition(
                projectCode,
                "idea-002",
                "[{\"taskCode\":14588264479296,\"x\":289,\"y\":46},{\"taskCode\":14588264479298,\"x\":66,\"y\":118}]",
                "[{\"name\":\"\",\"preTaskCode\":0,\"preTaskVersion\":0,\"postTaskCode\":14588264479296,\"postTaskVersion\":0,\"conditionType\":\"NONE\",\"conditionParams\":{}},{\"name\":\"\",\"preTaskCode\":14588264479296,\"preTaskVersion\":0,\"postTaskCode\":14588264479298,\"postTaskVersion\":0,\"conditionType\":\"NONE\",\"conditionParams\":{}}]",
                "[{\"code\":14588264479298,\"delayTime\":\"0\",\"description\":\"\",\"environmentCode\":-1,\"failRetryInterval\":\"1\",\"failRetryTimes\":\"0\",\"flag\":\"YES\",\"isCache\":\"NO\",\"name\":\"idea-002\",\"taskParams\":{\"localParams\":[],\"rawScript\":\"echo idea\",\"resourceList\":[]},\"taskPriority\":\"MEDIUM\",\"taskType\":\"SHELL\",\"timeout\":0,\"timeoutFlag\":\"CLOSE\",\"timeoutNotifyStrategy\":\"\",\"workerGroup\":\"default\",\"cpuQuota\":-1,\"memoryMax\":-1,\"taskExecuteType\":\"BATCH\"},{\"code\":14588264479296,\"delayTime\":\"0\",\"description\":\"\",\"environmentCode\":-1,\"failRetryInterval\":\"1\",\"failRetryTimes\":\"0\",\"flag\":\"YES\",\"isCache\":\"NO\",\"name\":\"idea-dep2\",\"taskParams\":{\"localParams\":[],\"resourceList\":[],\"dependence\":{\"checkInterval\":10,\"failurePolicy\":\"DEPENDENT_FAILURE_FAILURE\",\"relation\":\"AND\",\"dependTaskList\":[{\"relation\":\"AND\",\"dependItemList\":[{\"projectCode\":14576322429504,\"definitionCode\":14578042471232,\"depTaskCode\":0,\"cycle\":\"month\",\"dateValue\":\"thisMonth\",\"state\":null}]}]}},\"taskPriority\":\"MEDIUM\",\"taskType\":\"DEPENDENT\",\"timeout\":0,\"timeoutFlag\":\"CLOSE\",\"timeoutNotifyStrategy\":\"\",\"workerGroup\":\"default\",\"cpuQuota\":-1,\"memoryMax\":-1,\"taskExecuteType\":\"BATCH\"}]"
        );

        System.out.println(jsonObject);
    }

    @Test
    public void test04() throws Exception{
        JSONObject jsonObject = schedulerAPI.queryProcessDefinitionByCode(projectCode,14588333020736l);
        System.out.println(jsonObject);
    }


    @Test
    public void test05() throws Exception{

        long projectCode = 14576322429504l;
        String name = "idea-dep-update-idea";
        long code = 14588309409088l;

        String taskDefinitionJson = "[{\"id\":24,\"code\":14588264479296,\"name\":\"idea-dep-update\",\"version\":3,\"description\":\"\",\"projectCode\":14576322429504,\"userId\":1,\"taskType\":\"DEPENDENT\",\"taskParams\":{\"localParams\":[],\"resourceList\":[],\"dependence\":{\"checkInterval\":10,\"failurePolicy\":\"DEPENDENT_FAILURE_FAILURE\",\"relation\":\"AND\",\"dependTaskList\":[{\"relation\":\"AND\",\"dependItemList\":[{\"projectCode\":14576322429504,\"definitionCode\":14578042471232,\"depTaskCode\":14578035190720,\"cycle\":\"month\",\"dateValue\":\"thisMonth\",\"state\":null}]}]}},\"taskParamList\":[],\"taskParamMap\":null,\"flag\":\"YES\",\"isCache\":\"NO\",\"taskPriority\":\"MEDIUM\",\"userName\":null,\"projectName\":null,\"workerGroup\":\"default\",\"environmentCode\":-1,\"failRetryTimes\":0,\"failRetryInterval\":1,\"timeoutFlag\":\"CLOSE\",\"timeoutNotifyStrategy\":null,\"timeout\":0,\"delayTime\":0,\"resourceIds\":null,\"createTime\":\"2024-08-13 21:23:27\",\"updateTime\":\"2024-08-13 22:46:40\",\"modifyBy\":null,\"taskGroupId\":0,\"taskGroupPriority\":0,\"cpuQuota\":-1,\"memoryMax\":-1,\"taskExecuteType\":\"BATCH\",\"operator\":1,\"operateTime\":\"2024-08-13 22:46:40\",\"dependence\":\"{\\\"checkInterval\\\":10,\\\"failurePolicy\\\":\\\"DEPENDENT_FAILURE_FAILURE\\\",\\\"relation\\\":\\\"AND\\\",\\\"dependTaskList\\\":[{\\\"relation\\\":\\\"AND\\\",\\\"dependItemList\\\":[{\\\"projectCode\\\":14576322429504,\\\"definitionCode\\\":14578042471232,\\\"depTaskCode\\\":14578035190720,\\\"cycle\\\":\\\"month\\\",\\\"dateValue\\\":\\\"thisMonth\\\",\\\"state\\\":null}]}]}\"},{\"id\":25,\"code\":14588264479299,\"name\":\"idea-001-update\",\"version\":2,\"description\":\"\",\"projectCode\":14576322429504,\"userId\":1,\"taskType\":\"SHELL\",\"taskParams\":{\"localParams\":[],\"rawScript\":\"echo idea-update\",\"resourceList\":[]},\"taskParamList\":[],\"taskParamMap\":null,\"flag\":\"YES\",\"isCache\":\"NO\",\"taskPriority\":\"MEDIUM\",\"userName\":null,\"projectName\":null,\"workerGroup\":\"default\",\"environmentCode\":-1,\"failRetryTimes\":0,\"failRetryInterval\":1,\"timeoutFlag\":\"CLOSE\",\"timeoutNotifyStrategy\":null,\"timeout\":0,\"delayTime\":0,\"resourceIds\":null,\"createTime\":\"2024-08-13 21:23:27\",\"updateTime\":\"2024-08-13 22:46:40\",\"modifyBy\":null,\"taskGroupId\":0,\"taskGroupPriority\":0,\"cpuQuota\":-1,\"memoryMax\":-1,\"taskExecuteType\":\"BATCH\",\"operator\":1,\"operateTime\":\"2024-08-13 22:46:40\",\"dependence\":\"\"}]";

        String taskRelationJson = "[{\"name\":\"\",\"preTaskCode\":0,\"preTaskVersion\":0,\"postTaskCode\":14588264479296,\"postTaskVersion\":3,\"conditionType\":\"NONE\",\"conditionParams\":{}},{\"name\":\"\",\"preTaskCode\":14588264479296,\"preTaskVersion\":3,\"postTaskCode\":14588264479299,\"postTaskVersion\":2,\"conditionType\":\"NONE\",\"conditionParams\":{}}]";

        String locations = "[{\"taskCode\":14588264479296,\"x\":289,\"y\":46},{\"taskCode\":14588264479299,\"x\":66,\"y\":118}]";

//        JSONObject jsonObject = schedulerAPI.updateProcessDefinition(
//                projectCode,
//                name,
//                code,
//                locations,
//                taskRelationJson,
//                taskDefinitionJson
//        );

//        System.out.println(jsonObject);
    }

    @Test
    public void test06() throws Exception{
        JSONObject jsonObject = schedulerAPI.deleteProcessDefinitionByCode(projectCode, 14588333020736l);
        System.out.println(jsonObject);
    }


    @Test
    public void test07() throws Exception{
        JSONObject jsonObject = schedulerAPI.releaseProcessDefinition(projectCode, 14588309409088l,"ONLINE");
        System.out.println(jsonObject);
    }



    @Test
    public void test08() throws Exception{
        JSONObject jsonObject = schedulerAPI.queryWorkFlowLineageByCode(projectCode,14576351243712l);
        System.out.println(jsonObject);
    }


    @Test
    public void test09() throws Exception{
        JSONObject jsonObject = schedulerAPI.queryTaskDefinitionDetail(projectCode,14583311968576l);
        System.out.println(jsonObject);
    }


    @Test
    public void test10() throws Exception{
        JSONObject jsonObject = schedulerAPI.genTaskCodeList(projectCode, 1);
        System.out.println(jsonObject);

        Long data = jsonObject.getJSONArray("data").getLong(0);
        System.out.println(data);
    }

    @Test
    public void test11() throws Exception{
        System.out.println(Location.class.getSimpleName());
    }


    @Test
    public void test12() throws Exception{
        Map<String, BaseModel> modelMapper = BaseModel.getModelMapper();
        for (Map.Entry<String, BaseModel> entry :modelMapper.entrySet()){
            System.out.println(entry.getKey() + "  -->  " + entry.getValue());
        }
//        System.out.println(modelMapper);
        System.out.println(modelMapper.get(Location.class.getName()).getModel());
    }

    @Test
    public void test13() throws Exception{
        Map<String, BaseModel> modelMapper = BaseModel.getModelMapper();
        BaseModel baseModel = modelMapper.get(TaskRelation.class.getName());
        System.out.println(baseModel.toJSONObject());
        System.out.println(baseModel.getModel().toJSONObject());

        System.out.println(modelMapper.get(Location.class.getName()).getModel().toJSONObject());
    }


    @Test
    public void test14() throws Exception{
        ShellModel taskDefinitionTask = (ShellModel)BaseModel.getModelMapper().get(ShellModel.class.getName()).getModel();
        DependenceModel taskDefinitionDepend = (DependenceModel)BaseModel.getModelMapper().get(DependenceModel.class.getName()).getModel();

//        System.out.println(taskDefinitionTask.toJSONObject());
        System.out.println(taskDefinitionDepend.toJSONObject());
    }

    @Test
    public void test15(){
        System.out.println(new Random().nextInt(100));
    }


    /*

        name = schedulerModel.getTaskNodeName();
        executionType = schedulerModel.getExecutionType();
        description = schedulerModel.getDescription();
        globalParams = schedulerModel.getGlobalParams();
        timeout = schedulerModel.getTimeout();
     */
    @Test
    public void test16() throws Exception{
        DolphinSchedulerProjectInstance dolphinSchedulerInstance = new DolphinSchedulerProjectInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar");
        schedulerModel.setTaskNodeName("gawyn-test15");
        schedulerModel.setExecTaskScript("echo test15");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel = new DependencyModel("start", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel);
        schedulerModel.setDependency(dependency);

        JSONObject task = dolphinSchedulerInstance.createTask(schedulerModel);

    }


    @Test
    public void test17() throws Exception{
        DolphinSchedulerProjectInstance dolphinSchedulerInstance = new DolphinSchedulerProjectInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar");
        schedulerModel.setTaskNodeName("gawyn-test15");
        schedulerModel.setExecTaskScript("echo test15-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel = new DependencyModel("start", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.updateTask(schedulerModel);
        System.out.println("----------------- " + s);

    }


    @Test
    public void test18() throws Exception{
        DolphinSchedulerProjectInstance dolphinSchedulerInstance = new DolphinSchedulerProjectInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar");
        schedulerModel.setTaskNodeName("gawyn-test15");
        schedulerModel.setExecTaskScript("echo test15-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel = new DependencyModel("start", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.deleteTask(schedulerModel);
        System.out.println("----------------- " + s);

    }


    @Test
    public void test19() throws Exception{
        DolphinSchedulerProjectInstance dolphinSchedulerInstance = new DolphinSchedulerProjectInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar");
        schedulerModel.setTaskNodeName("gawyn-test15");
        schedulerModel.setExecTaskScript("echo test15-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel = new DependencyModel("start", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel);
        schedulerModel.setDependency(dependency);
        schedulerModel.setReleaseState(1);

//        String s = dolphinSchedulerInstance.deployTask(schedulerModel);
//        System.out.println("----------------- " + s);
        JSONObject release = dolphinSchedulerInstance.release(schedulerModel);
        System.out.println(release);

    }


    @Test
    public void test20() throws Exception{
        DolphinSchedulerProjectInstance dolphinSchedulerInstance = new DolphinSchedulerProjectInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar");
        schedulerModel.setTaskNodeName("gawyn-test15");
        schedulerModel.setExecTaskScript("echo test15-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel = new DependencyModel("start", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.timingTask(schedulerModel);
        System.out.println("----------------- " + s);

    }



    @Test
    public void test21() throws Exception{
        DolphinSchedulerProjectInstance dolphinSchedulerInstance = new DolphinSchedulerProjectInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar");
        schedulerModel.setTaskNodeName("gawyn-test15");
        schedulerModel.setExecTaskScript("echo test15-15-15");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel = new DependencyModel("start", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.deployTask(schedulerModel);
        System.out.println("----------------- " + s);

    }


    @Test
    public void test22() throws Exception{
        DolphinSchedulerProjectInstance dolphinSchedulerInstance = new DolphinSchedulerProjectInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar");
        schedulerModel.setTaskNodeName("gawyn-test15");
        schedulerModel.setExecTaskScript("echo test15-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel = new DependencyModel("start", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.deleteTask(schedulerModel);
        System.out.println("----------------- " + s);

    }




    @Test
    public void test31() throws Exception{
        DolphinSchedulerWorkflowInstance dolphinSchedulerInstance = new DolphinSchedulerWorkflowInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar___caesar");
        schedulerModel.setTaskNodeName("idea-001");
        schedulerModel.setExecTaskScript("echo idea-001");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel1 = new DependencyModel("level1-1", SchedulingPeriod.DAY, "today");
        DependencyModel dependencyModel2 = new DependencyModel("level1-2", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel1);
        dependency.add(dependencyModel2);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.createTask(schedulerModel);
        System.out.println("----------------- " + s);

    }


    @Test
    public void test32() throws Exception{
        DolphinSchedulerWorkflowInstance dolphinSchedulerInstance = new DolphinSchedulerWorkflowInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar___caesar");
        schedulerModel.setTaskNodeName("idea-001");
        schedulerModel.setExecTaskScript("echo idea-001-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel1 = new DependencyModel("level1-1", SchedulingPeriod.DAY, "today");
        DependencyModel dependencyModel2 = new DependencyModel("level1-2", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel1);
        dependency.add(dependencyModel2);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.updateTask(schedulerModel);
        System.out.println("----------------- " + s);

    }


    @Test
    public void test33() throws Exception{
        DolphinSchedulerWorkflowInstance dolphinSchedulerInstance = new DolphinSchedulerWorkflowInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar___caesar");
        schedulerModel.setTaskNodeName("idea-001");
        schedulerModel.setExecTaskScript("echo idea-001-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel1 = new DependencyModel("level1-1", SchedulingPeriod.DAY, "today");
        DependencyModel dependencyModel2 = new DependencyModel("level1-2", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel1);
        dependency.add(dependencyModel2);
        schedulerModel.setDependency(dependency);

        JSONObject s = dolphinSchedulerInstance.deleteTask(schedulerModel);
        System.out.println("----------------- " + s);

    }



    @Test
    public void test34() throws Exception{
        DolphinSchedulerWorkflowInstance dolphinSchedulerInstance = new DolphinSchedulerWorkflowInstance();
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar___caesar");
        schedulerModel.setTaskNodeName("idea-001");
        schedulerModel.setExecTaskScript("echo idea-001-update1");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel1 = new DependencyModel("level1-1", SchedulingPeriod.DAY, "today");
        DependencyModel dependencyModel2 = new DependencyModel("level1-2", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel1);
        dependency.add(dependencyModel2);
        schedulerModel.setDependency(dependency);

        schedulerModel.setDelete(true);
        JSONObject s = dolphinSchedulerInstance.deployTask(schedulerModel);
        System.out.println("----------------- " + s);

    }



    @Test
    public void test35() throws Exception{

        SchedulerFacade schedulerFacade = SchedulerUtils.getScheduler();

        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setProject("caesar___caesar");
        schedulerModel.setTaskNodeName("idea-001");
        schedulerModel.setExecTaskScript("echo idea-001-update1-update");

        List<DependencyModel> dependency = new ArrayList<>();
        DependencyModel dependencyModel1 = new DependencyModel("level1-1", SchedulingPeriod.DAY, "today");
        DependencyModel dependencyModel2 = new DependencyModel("level1-2", SchedulingPeriod.DAY, "today");
        dependency.add(dependencyModel1);
        dependency.add(dependencyModel2);
        schedulerModel.setDependency(dependency);

        schedulerModel.setDelete(true);
        ScheduleResponse response = schedulerFacade.deployTask(schedulerModel);
        System.out.println("----------------- " + response);

    }

}
