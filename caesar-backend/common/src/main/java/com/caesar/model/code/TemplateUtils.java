package com.caesar.model.code;

import com.alibaba.fastjson.JSONObject;
import com.caesar.enums.EngineEnum;
import com.caesar.exception.EngineNotDefineException;
import com.caesar.model.code.config.TemplateConfig;
import com.caesar.model.code.enums.DatePeriod;
import com.caesar.model.code.enums.Parameters;
import com.caesar.model.code.model.*;
import com.caesar.util.DateUtils;
import com.caesar.util.JSONUtils;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.UIManager.getString;

public class TemplateUtils {

    /**
     * 生成请求参数,封装预定义参数信息
     *
     * @return
     */
    public static Map<String,String> generalRefreshParameter(DatePeriod period, Date requestDate){

        Map<String,String> parameter = new HashMap<>();

        try {
            parameter.put("period", period.getKey());
            parameter.put("etl_date", Parameters.fromParameter("etl_date").applyExpression(period,requestDate));
            parameter.put("start_date", Parameters.fromParameter("start_date").applyExpression(period,requestDate));
            parameter.put("end_date", Parameters.fromParameter("end_date").applyExpression(period,requestDate));
        }catch (Exception e){
            e.printStackTrace();
        }

        return parameter;
    }



    public static ExecuteScript transformSqlTemplate(String template) throws EngineNotDefineException{

        TaskContentParser taskContentParser = new TaskContentParser(template);
        MetaContent metaContent = taskContentParser.getTaskContentModel().getMetaContent();
        ParamsConfig paramsConfig = taskContentParser.getTaskContentModel().getParamsConfig();
        SchemaDefine schemaDefine = taskContentParser.getTaskContentModel().getSchemaDefine();
        EtlProcess etlProcess = taskContentParser.getTaskContentModel().getEtlProcess();

        ExecuteScript executeScript = new ExecuteScript();
        StringBuffer scriptBuffer = new StringBuffer();
        EngineEnum engine = metaContent.getEngine();
        executeScript.setTaskName(metaContent.getTaskName());
        executeScript.setEngine(engine);

        switch (engine){
            case HIVE:
                // 1 parse system parameters ,not execute
                List<CaesarParams> systemParamsHive = paramsConfig.getSystemParams();
                if(systemParamsHive.size()>0) {
                    Map<String, String> systemMap = new HashMap<>();
                    for (CaesarParams systemParam : systemParamsHive) {
                        Pair pair = (Pair) systemParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        systemMap.put(key, value);
                    }
                    executeScript.setSystemParams(systemMap);
                }

                // 2 parse engine parameters ,not execute
                List<CaesarParams> engineParamsHive = paramsConfig.getEngineParams();
                if(engineParamsHive.size()>0) {
                    Map<String, String> engineMap = new HashMap<>();
                    for (CaesarParams caesarParam : engineParamsHive) {
                        Pair pair = (Pair) caesarParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        engineMap.put(key, value);
                    }
                    executeScript.setEngineParams(engineMap);
                }

                // 3 parse custom parameters ,execute
                List<CaesarParams> customParamsHive = paramsConfig.getCustomParams();
                if(customParamsHive.size()>0) {
                    Map<String, String> customVariableMap = new HashMap<>();
                    for (CaesarParams customParam : customParamsHive) {
                        CaesarParams.ParamsType type = customParam.getType();
                        switch (type) {
                            case Pair:
                                Pair pair = (Pair) customParam;
//                                String key = pair.getKey().trim();
//                                String value = pair.getValue().trim();
//                                customVariableMap.put(key,value);

                                if (pair.getValue().startsWith("${") && pair.getValue().endsWith("}")) {
                                    String variable = pair.getValue().replaceAll("\\$\\{", "").replaceAll("\\}", "");
                                    customVariableMap.put(variable, pair.getValue());
                                }

                                if (pair.getKey().startsWith("hivevar:") || pair.getKey().startsWith("hiveconf:")) {
                                    scriptBuffer
                                            .append("set")
                                            .append(" ")
                                            .append(pair.getKey())
                                            .append(" = ")
                                            .append(pair.getValue())
                                            .append(";")
                                            .append("\n");
                                } else {
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
                                CustomFunctionParams customFunctionParams = (CustomFunctionParams) customParam;
                                if (customFunctionParams.getStatement().trim().endsWith(";")) {
                                    scriptBuffer.append(customFunctionParams.getStatement().replaceAll("\\s"," ").trim()).append("\n");
                                } else {
                                    scriptBuffer.append(customFunctionParams.getStatement().replaceAll("\\s"," ").trim()).append(";").append("\n");
                                }
                                break;
                        }
                    }
                    executeScript.setCustomParamValues(customVariableMap);
                }
                break;
            case SPARK:
                // 1 parse system parameters ,not execute
                List<CaesarParams> systemParamsSpark = paramsConfig.getSystemParams();
                if(systemParamsSpark.size()>0) {
                    Map<String, String> systemMap = new HashMap<>();
                    for (CaesarParams systemParam : systemParamsSpark) {
                        Pair pair = (Pair) systemParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        systemMap.put(key, value);
                    }
                    executeScript.setSystemParams(systemMap);
                }

                // 2 parse engine parameters ,not execute
                List<CaesarParams> engineParamsSpark = paramsConfig.getEngineParams();
                if(engineParamsSpark.size()>0) {
                    Map<String, String> engineMap = new HashMap<>();
                    for (CaesarParams caesarParam : engineParamsSpark) {
                        Pair pair = (Pair) caesarParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        engineMap.put(key, value);
                    }
                    executeScript.setEngineParams(engineMap);
                }

                // 3 parse custom parameters ,execute
                List<CaesarParams> customParamsSpark = paramsConfig.getCustomParams();
                if(customParamsSpark.size()>0) {
                    Map<String, String> customVariableMap = new HashMap<>();
                    for (CaesarParams customParam : customParamsSpark) {
                        CaesarParams.ParamsType type = customParam.getType();
                        switch (type) {
                            case Pair:
                                Pair pair = (Pair) customParam;
//                                String key = pair.getKey().trim();
//                                String value = pair.getValue().trim();
//                                customVariableMap.put(key,value);

                                if (pair.getValue().startsWith("${") && pair.getValue().endsWith("}")) {
                                    String variable = pair.getValue().replaceAll("\\$\\{", "").replaceAll("\\}", "");
                                    customVariableMap.put(variable, pair.getValue());
                                }

                                scriptBuffer
                                        .append("set")
                                        .append(" ")
                                        .append(pair.getKey())
                                        .append(" = ")
                                        .append(pair.getValue())
                                        .append(";")
                                        .append("\n");
                                break;
                            case customFunction:
                                CustomFunctionParams customFunctionParams = (CustomFunctionParams) customParam;
                                if (customFunctionParams.getStatement().trim().endsWith(";")) {
                                    scriptBuffer.append(customFunctionParams.getStatement().replaceAll("\\s"," ").trim()).append("\n");
                                } else {
                                    scriptBuffer.append(customFunctionParams.getStatement().replaceAll("\\s"," ").trim()).append(";").append("\n");
                                }
                                break;
                        }
                    }
                    executeScript.setCustomParamValues(customVariableMap);
                }
                break;
            case DORIS:
                // 1 parse system parameters ,not execute
                List<CaesarParams> systemParamsDoris = paramsConfig.getSystemParams();
                if(systemParamsDoris.size()>0) {
                    Map<String, String> systemMap = new HashMap<>();
                    for (CaesarParams systemParam : systemParamsDoris) {
                        Pair pair = (Pair) systemParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        systemMap.put(key, value);
                    }
                    executeScript.setSystemParams(systemMap);
                }

                // 2 parse engine parameters ,not execute
                List<CaesarParams> engineParamsDoris = paramsConfig.getEngineParams();
                if(engineParamsDoris.size()>0) {
                    Map<String, String> engineMap = new HashMap<>();
                    for (CaesarParams caesarParam : engineParamsDoris) {
                        Pair pair = (Pair) caesarParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        engineMap.put(key, value);
                    }
                    executeScript.setEngineParams(engineMap);
                }

                // 3 parse custom parameters ,execute
                List<CaesarParams> customParamsDoris = paramsConfig.getCustomParams();
                if(customParamsDoris.size()>0) {
                    Map<String, String> customVariableMap = new HashMap<>();
                    for (CaesarParams customParam : customParamsDoris) {
                        Pair pair = (Pair) customParam;
                        String key = pair.getKey().trim();
                        String value = pair.getValue().trim();
                        customVariableMap.put(key,value);

                        if (pair.getValue().startsWith("${") && pair.getValue().endsWith("}")) {
                            String variable = pair.getValue().replaceAll("\\$\\{", "").replaceAll("\\}", "");
                            customVariableMap.put(variable, pair.getValue());
                        }
                    }
                    executeScript.setCustomParamValues(customVariableMap);
                }
                break;
            default:
                throw new EngineNotDefineException("模板定义引擎"+engine.getEngine()+"暂时不被支持");
        }

        // parse schema and etl
        scriptBuffer.append(schemaDefine.getContent()).append("\n");
        scriptBuffer.append(etlProcess.getContent());

        executeScript.setScript(scriptBuffer.toString());
        executeScript.setSchema(schemaDefine.getContent());
        executeScript.setCode(etlProcess.getContent());

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
        String taskName;
        EngineEnum engine;
        Map<String, String> systemParams;
        Map<String, String> engineParams;
        Map<String, String> customParamValues;
        String script;
        String schema;
        String code;
    }
}
