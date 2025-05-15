package com.caesar.util;

import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.runner.params.TaskInfo;
import com.caesar.text.model.ScriptInfo;

import java.util.HashMap;
import java.util.logging.Logger;

public class EngineUtils {

    public static final Logger logger = Logger.getLogger(EngineUtils.class.getName());

    public static String getTaskShellExecuteScript(TaskInfo taskInfo){

        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.TEXT);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        ScriptInfo scriptInfo = engine.buildCodeScript(taskInfo);

        String prodSqlFile = scriptInfo.getProdSqlFile();
        String prodScriptFile = "sh " + scriptInfo.getProdScriptFile();

        logger.info("");
        logger.info(String.format("本次部署任务对应调度Shell脚本: \n%s",prodScriptFile));
        logger.info(String.format("本次部署任务对应SQL脚本: \n%s",prodSqlFile));
        logger.info("");

        return prodScriptFile;

    }


}
