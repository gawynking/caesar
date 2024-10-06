package com.caesar.model.code;

import com.caesar.enums.EngineEnum;
import com.caesar.exception.EngineNotDefineException;
import com.caesar.model.code.model.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.UIManager.getString;

public class TemplateUtils {


    public static ExecuteScript transformSqlTemplate(String template) throws EngineNotDefineException{
        ExecuteScript executeScript = new ExecuteScript();
        StringBuffer scriptBuffer = new StringBuffer();
        TaskContentParser taskContentParser = new TaskContentParser(template);
        MetaContent metaContent = taskContentParser.getTaskContentModel().getMetaContent();
        ParamsConfig paramsConfig = taskContentParser.getTaskContentModel().getParamsConfig();
        SchemaDefine schemaDefine = taskContentParser.getTaskContentModel().getSchemaDefine();
        EtlProcess etlProcess = taskContentParser.getTaskContentModel().getEtlProcess();


        EngineEnum engine = metaContent.getEngine();
        executeScript.setEngine(engine);
        switch (engine){
            case HIVE:
            case SPARK:
                // 1 parse system parameters ,not execute
                List<CaesarParams> systemParams = paramsConfig.getSystemParams();
                if(systemParams.size()>0) {
                    Map<String, String> systemMap = new HashMap<>();
                    for (CaesarParams systemParam : systemParams) {
                        Pair pair = (Pair) systemParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        systemMap.put(key, value);
                    }
                    executeScript.setSystemParams(systemMap);
                }

                // 2 parse engine parameters ,not execute
                List<CaesarParams> engineParams = paramsConfig.getEngineParams();
                if(engineParams.size()>0) {
                    Map<String, String> engineMap = new HashMap<>();
                    for (CaesarParams caesarParam : engineParams) {
                        Pair pair = (Pair) caesarParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        engineMap.put(key, value);
                    }
                    executeScript.setEngineParams(engineMap);
                }

                // 3 parse custom parameters ,execute
                List<CaesarParams> customParams = paramsConfig.getCustomParams();
                Map<String, Integer> customVariableMap = new HashMap<>();
                for(CaesarParams customParam:customParams){
                    CaesarParams.ParamsType type = customParam.getType();
                    switch (type){
                        case Pair:
                            Pair pair = (Pair)customParam;
                            if(pair.getValue().startsWith("${") && pair.getValue().endsWith("}")){
                                String variable = pair.getValue().replaceAll("\\$\\{","").replaceAll("\\}","");
                                int count = 0;
                                if(customVariableMap.containsKey(variable)){
                                    count = customVariableMap.get(variable);
                                }
                                customVariableMap.put(variable,count+1);
                            }
                            if(pair.getKey().startsWith("hivevar:") || pair.getKey().startsWith("hiveconf:")) {
                                scriptBuffer
                                        .append("set")
                                        .append(" ")
                                        .append(pair.getKey())
                                        .append(" = ")
                                        .append(pair.getValue())
                                        .append(";")
                                        .append("\n");
                            }else {
                                scriptBuffer
                                        .append("set")
                                        .append(" ")
                                        .append("hivevar:")
                                        .append(pair.getKey())
                                        .append(" = ")
                                        .append(parseAndReplaceVariablesForHive(pair.getValue()))
                                        .append(";")
                                        .append("\n");
                            }
                            break;
                        case customFunction:
                            CustomFunctionParams customFunctionParams = (CustomFunctionParams)customParam;
                            if(customFunctionParams.getStatement().trim().endsWith(";")) {
                                scriptBuffer.append(customFunctionParams.getStatement()).append("\n");
                            }else {
                                scriptBuffer.append(customFunctionParams.getStatement()).append(";").append("\n");
                            }
                            break;
                    }
                }
                executeScript.setCustomParamValues(customVariableMap);
                break;
            case DORIS:
                throw new EngineNotDefineException("模板定义引擎"+engine.getEngine()+"暂时不被支持");
            default:
                throw new EngineNotDefineException("模板定义引擎"+engine.getEngine()+"暂时不被支持");
        }

        // parse schema and etl
        scriptBuffer.append(schemaDefine.getContent()).append("\n");
        scriptBuffer.append(etlProcess.getContent());

        executeScript.setScript(scriptBuffer.toString());

        return executeScript;
    }


    /**
     * 返回映射后的变量集合
     *
     * @param statement
     * @return
     */
    private static Map<String, String> parseAndReturnVariablesForHive(String statement) {
        // 正则表达式匹配 ${...} 中的变量, 包括 ${etl_date} 或 ${hivevar:etl_date}
        String variableRegex = "\\$\\{([^}]+)}";
        Pattern pattern = Pattern.compile(variableRegex);
        Matcher matcher = pattern.matcher(statement);
        Map<String, String> newVariableMap = new HashMap<>();
        while (matcher.find()) {
            String variableName = matcher.group(1); // 捕获变量名，如 hivevar:etl_date 或 etl_date
            String replacementValue = "\\$\\{"+variableName+"\\}"; // 保持原样 ${hivevar:owner} 或 ${owner}
            if(!variableName.startsWith("hivevar:") && !variableName.startsWith("hiveconf:")){
                replacementValue = "\\$\\{hivevar:"+variableName+"\\}";
            }
            newVariableMap.put(variableName,replacementValue);
        }
        return newVariableMap;
    }

    /**
     * 直接根据解析结果替换，并且返回结果
     *
     * @param statement
     * @return
     */
    private static String parseAndReplaceVariablesForHive(String statement) {
        // 正则表达式匹配 ${...} 中的变量, 包括 ${etl_date} 或 ${hivevar:etl_date}
        String variableRegex = "\\$\\{([^}]+)}";
        Pattern pattern = Pattern.compile(variableRegex);
        Matcher matcher = pattern.matcher(statement);

        // 用于保存替换后的语句
        StringBuffer modifiedStatement = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1); // 捕获变量名，如 hivevar:etl_date 或 etl_date
            String replacementValue = "\\$\\{"+variableName+"\\}"; // 保持原样 ${hivevar:owner} 或 ${owner}
            if(!variableName.startsWith("hivevar:") && !variableName.startsWith("hiveconf:")){
                replacementValue = "\\$\\{hivevar:"+variableName+"\\}";
            }

            // 替换变量
            matcher.appendReplacement(modifiedStatement, replacementValue);
        }
        matcher.appendTail(modifiedStatement);
        return modifiedStatement.toString();
    }


    @Data
    public static class ExecuteScript{
        EngineEnum engine;
        Map<String, String> systemParams;
        Map<String, String> engineParams;
        Map<String, Integer> customParamValues;
        String script;
    }
}
