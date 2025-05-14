package com.caesar.util;

import com.caesar.engine.Engine;
import com.caesar.enums.EngineEnum;
import com.caesar.factory.EngineFactory;
import com.caesar.factory.EngineFactoryRegistry;
import com.caesar.runner.params.TaskInfo;
import com.caesar.text.model.ScriptInfo;

import java.util.HashMap;

public class EngineUtils {


    public static String getTaskExecuteScript(TaskInfo taskInfo){

        EngineFactory engineFactory = new EngineFactoryRegistry().getEngineFactory(EngineEnum.TEXT);
        Engine engine = engineFactory.createEngine(new HashMap<>());
        ScriptInfo scriptInfo = engine.buildCodeScript(taskInfo);

        String prodSqlFile = scriptInfo.getProdSqlFile();
        String prodScriptFile = scriptInfo.getProdScriptFile();

        return prodScriptFile;

    }


}
