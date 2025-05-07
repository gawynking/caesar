package com.caesar.runner.params;

import com.caesar.enums.EngineEnum;
import com.caesar.enums.EnvironmentEnum;
import com.caesar.model.code.enums.DatePeriod;
import lombok.Data;

import java.util.Map;

/**
 * 请求参数
 */
@Data
public class TaskInfo {

    private EngineEnum engine; // 执行引擎
    private String systemUser; // 执行操作系统用户
    private EnvironmentEnum environment; // 执行环境 test prod
    private DatePeriod period; // 调度周期
    private String dbLevel; // 目标数据库
    private String taskName; // 任务名称
    private String code; // 可执行代码
    private Map<String, String> config; // 任务配置信息
    private Map<String, String> taskInputParams; // 任务传入参数
    private Map<String,String> customParamValues; // 代码解析参数
    private Map<String, String> engineParams; // 引擎级别参数

}
