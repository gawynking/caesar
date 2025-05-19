package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.config.PublishConfig;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.CaesarTaskExecutePlan;
import com.caesar.entity.CaesarTaskExecuteRecord;
import com.caesar.entity.CaesarTaskTestCase;
import com.caesar.entity.dto.CaesarTaskExecuteRecordDto;
import com.caesar.entity.vo.request.VerificationTestingVo;
import com.caesar.enums.EnvironmentEnum;
import com.caesar.mapper.*;
import com.caesar.runner.ExecutionResult;
import com.caesar.runner.Executor;
import com.caesar.service.TaskExecuteService;
import com.caesar.runner.params.TaskInfo;
import com.caesar.shell.ShellTask;
import com.caesar.task.Task;
import com.caesar.util.TaskUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
public class TaskExecuteServiceImpl extends ServiceImpl<TaskExecuteMapper, CaesarTaskExecuteRecord> implements TaskExecuteService {

    private static final Logger logger = Logger.getLogger(TaskExecuteServiceImpl.class);

    private static final ExecutorService workService = Executors.newFixedThreadPool((Integer) ((Map) new PublishConfig().getCaesarConfig().get("engine")).get("max-parallel-number"));


    @Resource
    TaskExecutePlanMapper taskExecutePlanMapper;

    @Resource
    TaskExecuteMapper taskExecuteMapper;

    @Resource
    TaskMapper taskMapper;

    @Resource
    DatasourceMapper datasourceMapper;

    @Resource
    TaskTestCaseMapper testCaseMapper;

    @Override
    public Boolean saveExecutePlan(CaesarTaskExecutePlan taskExecutePlan) {
        return taskExecutePlanMapper.save(taskExecutePlan);
    }

    @Override
    public Boolean execute(CaesarTaskExecuteRecordDto taskExecuteRecordDto) {

        String parameter = taskExecuteRecordDto.getParameter();
        EnvironmentEnum environment = EnvironmentEnum.fromKey(taskExecuteRecordDto.getEnvironment());

        CaesarTask caesarTask = taskMapper.getTaskInfoFromId(taskExecuteRecordDto.getTaskId());
        TaskInfo task = TaskUtils.generalTaskInfo(taskExecuteRecordDto, caesarTask);

        CaesarTaskExecuteRecord taskExecuteRecord = new CaesarTaskExecuteRecord();
        taskExecuteRecord.setTaskId(caesarTask.getId());
//        taskExecuteRecord.setUuid(UUID.randomUUID().toString().toLowerCase().replaceAll("-",""));
        taskExecuteRecord.setUuid(taskExecuteRecordDto.getUuid());
        taskExecuteRecord.setTaskName(caesarTask.getTaskName());
        taskExecuteRecord.setEnvironment(environment.getKey());
        taskExecuteRecord.setBeginTime(LocalDateTime.now());
        taskExecuteRecord.setIsSuccess(0);
        taskExecuteRecord.setParameter(parameter);
        taskExecuteRecord.setPlanUuid(taskExecuteRecordDto.getPlanUuid());

        Integer id = null;
        switch (environment) {
            case TEST:
                taskExecuteMapper.insert(taskExecuteRecord);
                id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
                taskExecuteRecord.setId(id);
                try {
                    ExecutionResult<ShellTask> job = Executor.execute(task); // 执行测试任务
                    ShellTask jobTask = job.getData();
                    if (jobTask.getStatus().isSuccess()) {
                        taskExecuteRecord.setEndTime(LocalDateTime.now());
                        taskExecuteRecord.setIsSuccess(1);
                    } else {
                        taskExecuteRecord.setEndTime(LocalDateTime.now());
                        taskExecuteRecord.setIsSuccess(0);
                    }
                    jobTask.cancel();
                } catch (Exception e) {
                    taskExecuteRecord.setIsSuccess(0);
                    e.printStackTrace();
                }
                taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(), taskExecuteRecord.getEndTime(), taskExecuteRecord.getId());
                break;
            case PRODUCTION:
                boolean isOnline = null != taskMapper.checkOnlineById(taskExecuteRecord.getTaskId()) ? true : false;
                if (isOnline) {
                    taskExecuteMapper.insert(taskExecuteRecord);
                    id = taskExecuteMapper.findIdFromUUID(taskExecuteRecord);
                    taskExecuteRecord.setId(id);
                    try {
                        ExecutionResult<ShellTask> job = Executor.execute(task); // 执行任务
                        ShellTask jobTask = job.getData();
                        if (jobTask.getStatus().isSuccess()) {
                            taskExecuteRecord.setEndTime(LocalDateTime.now());
                            taskExecuteRecord.setIsSuccess(1);
                        } else {
                            taskExecuteRecord.setEndTime(LocalDateTime.now());
                            taskExecuteRecord.setIsSuccess(0);
                        }
                        jobTask.cancel();
                    } catch (Exception e) {
                        taskExecuteRecord.setIsSuccess(0);
                        e.printStackTrace();
                    }
                    taskExecuteMapper.updateExecuteState(taskExecuteRecord.getIsSuccess(), taskExecuteRecord.getEndTime(), taskExecuteRecord.getId());
                } else {
                    logger.info(String.format("不能执行未发版任务"));
                    return false;
                }
                break;
        }

        return true;
    }


    @Override
    public Boolean refresh(List<CaesarTaskExecuteRecordDto> taskExecuteRecordDtos) {

        this.workService.submit(() -> {
            for (CaesarTaskExecuteRecordDto taskExecuteRecordDto : taskExecuteRecordDtos) {
                taskExecutePlanMapper.updatePlanStatus(taskExecuteRecordDto.getPlanUuid(), 2);
                this.execute(taskExecuteRecordDto);
            }

            List<CaesarTaskExecuteRecord> taskExecuteRecords = taskExecuteMapper.findRecordByPlan(taskExecuteRecordDtos.get(0).getPlanUuid());
            if (null != taskExecuteRecords && taskExecuteRecords.size() == taskExecuteRecordDtos.size()) {
                taskExecutePlanMapper.updatePlanStatus(taskExecuteRecordDtos.get(0).getPlanUuid(), 3);
                if(taskExecuteRecordDtos.get(0).getEnvironment().equals("test")){
                    CaesarTask tmpTaskInfo = taskMapper.getTaskInfoFromId(taskExecuteRecordDtos.get(0).getTaskId());
                    this.executeTestCase(tmpTaskInfo.getTaskName(), tmpTaskInfo.getVersion(), taskExecuteRecordDtos.get(0).getUserId());
                }
            } else {
                taskExecutePlanMapper.updatePlanStatus(taskExecuteRecordDtos.get(0).getPlanUuid(), 4);
            }

        });

        return true;
    }

    @Override
    public Boolean validateTaskIsPassedTest(int taskId) {
        return taskExecuteMapper.validateTaskIsPassedTest(taskId);
    }

    @Override
    public Boolean executeTestCase(String taskName, int taskVersion, Integer userId) {

        // 带用例版本暂不考虑支持
//        String testCode = String.format(
//                "select 'prod' as tag, count(1) as cnt from %s " +
//                "union all " +
//                "select 'test' as tag, count(1) from %s");

        CaesarTaskTestCase taskTestCase = new CaesarTaskTestCase();
        taskTestCase.setUuid(UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
        taskTestCase.setTaskId(taskMapper.getTaskIdByVersion(taskVersion));
        taskTestCase.setTaskName(taskName);
        taskTestCase.setTaskVersion(taskVersion);
        taskTestCase.setUserId(userId);
        taskTestCase.setTestCode(""); // 暂不支持用例
        taskTestCase.setTestResult(0);

        testCaseMapper.saveTestCase(taskTestCase);

        return true;
    }

    @Override
    public List<CaesarTaskTestCase> getTestCases(Integer userId, String taskName, Integer testResult) {
        return testCaseMapper.getTestCases(userId, taskName, testResult);
    }

    @Override
    public Boolean verificationTesting(VerificationTestingVo verificationTestingVo) {
        return testCaseMapper.verificationTesting(verificationTestingVo);
    }

}
