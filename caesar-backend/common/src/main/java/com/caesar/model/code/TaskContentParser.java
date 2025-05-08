package com.caesar.model.code;


import com.caesar.enums.EngineEnum;
import com.caesar.enums.ParamsCategoryEnum;
import com.caesar.enums.TemplateSectionEnum;
import com.caesar.model.code.model.*;
import com.caesar.util.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class TaskContentParser {

    private static final Logger logger = Logger.getLogger(TaskContentParser.class);

    private TaskContentModel taskContentModel;

    public TaskContentParser(String content){
        this.taskContentModel = parseScriptContent(content);
    }

    public TaskContentModel getTaskContentModel(){
        return this.taskContentModel;
    }

    private String getString(List<String> list){
        StringBuffer stringBuffer = new StringBuffer();
        for(String line :list){
            stringBuffer.append(line).append("\n");
        }
        if(stringBuffer.length()>=1){
            return stringBuffer.deleteCharAt(stringBuffer.length()-1).toString();
        }
        return stringBuffer.toString();
    }


    // 解析模板内容
    private TaskContentModel parseScriptContent(String content) {

        TaskContentModel taskContentModel = new TaskContentModel();

        List<String> meta = new ArrayList<>();
        ParamsModel params = new ParamsModel();
        List<String> schema = new ArrayList();
        List<String> etl = new ArrayList();

        TemplateSectionEnum flag = TemplateSectionEnum.OTHER;
        ParamsCategoryEnum paramsCategory = ParamsCategoryEnum.OTHER;
        String[] lines = content.split("\n");
        for(String line :lines){
            line = line.trim();
            if(line.replaceAll("\\s","").toUpperCase().startsWith("###META#########")){
                flag = TemplateSectionEnum.META;
                continue;
            } else if (line.replaceAll("\\s","").toUpperCase().startsWith("###PARTONE#########")) {
                flag = TemplateSectionEnum.PARAMS;
                continue;
            }else if (line.replaceAll("\\s","").toUpperCase().startsWith("###PARTTWO#########")) {
                flag = TemplateSectionEnum.SCHEMA;
                continue;
            }else if (line.replaceAll("\\s","").toUpperCase().startsWith("###PARTTHREE#########")) {
                flag = TemplateSectionEnum.ETL;
                continue;
            }

            if(line.replaceAll("\\s","").toUpperCase().startsWith("##系统参数")){
                paramsCategory = ParamsCategoryEnum.SYSTEM;
                continue;
            } else if (line.replaceAll("\\s","").toUpperCase().startsWith("##引擎参数")) {
                paramsCategory = ParamsCategoryEnum.ENGINE;
                continue;
            } else if (line.replaceAll("\\s","").toUpperCase().startsWith("##自定义UDF&参数")) {
                paramsCategory = ParamsCategoryEnum.CUSTOM;
                continue;
            }

            if(TemplateSectionEnum.META == flag && !line.replaceAll("\\s","").startsWith("#") && !line.replaceAll("\\s","").startsWith("--") && StringUtils.isNotEmpty(line.replaceAll("\\s","").trim())){
                meta.add(line);
            } else if(TemplateSectionEnum.PARAMS == flag && !line.replaceAll("\\s","").startsWith("#") && !line.replaceAll("\\s","").startsWith("--") && StringUtils.isNotEmpty(line.replaceAll("\\s","").trim())){
                switch (paramsCategory){
                    case SYSTEM:
                        params.addSystemParams(line);
                        break;
                    case ENGINE:
                        params.addEngineParams(line);
                        break;
                    case CUSTOM:
                        params.addCustomParams(line);
                        break;
                }
            } else if(TemplateSectionEnum.SCHEMA == flag && !line.replaceAll("\\s","").startsWith("#") && !line.replaceAll("\\s","").startsWith("--")){
                schema.add(line);
            } else if(TemplateSectionEnum.ETL == flag && !line.replaceAll("\\s","").startsWith("#") && !line.replaceAll("\\s","").startsWith("--")){
                etl.add(line);
            }
        }

        MetaContent metaContent = parseMeta(meta);
        ParamsConfig paramsConfig = parseParams(params);
        SchemaDefine schemaDefine = parseSchema(schema);
        EtlProcess etlProcess = parseEtl(etl);

        taskContentModel.setMetaContent(metaContent);
        taskContentModel.setParamsConfig(paramsConfig);
        taskContentModel.setSchemaDefine(schemaDefine);
        taskContentModel.setEtlProcess(etlProcess);

        return taskContentModel;
    }


    private EtlProcess parseEtl(List<String> etlList){
        EtlProcess etlProcess = new EtlProcess();
        String content = "";
        StringBuffer sb = new StringBuffer();
        for(String line :etlList){
            sb.append(line).append("\n");
        }
        content = sb.deleteCharAt(sb.length()-1).toString();
        etlProcess.setContent(content);
        return etlProcess;
    }

    private SchemaDefine parseSchema(List<String> schemaList){
        SchemaDefine schemaDefine = new SchemaDefine();
        String content = "";
        StringBuffer sb = new StringBuffer();
        for(String line :schemaList){
            sb.append(line).append("\n");
        }
        content = sb.deleteCharAt(sb.length()-1).toString();
        schemaDefine.setContent(content);
        return schemaDefine;
    }

    private ParamsConfig parseParams(ParamsModel paramsModel){
        ParamsConfig paramsConfig = new ParamsConfig();

        String content;
        List<CaesarParams> systemParams = new ArrayList<>();
        List<CaesarParams> engineParams = new ArrayList<>();
        List<CaesarParams> customParams = new ArrayList<>();

        StringBuffer context = new StringBuffer();

        // 1 System Parameters
        StringBuffer systemBuffer = new StringBuffer();
        for(String line:paramsModel.getSystemParams()){
            context.append(line).append("\n");
            systemBuffer.append(line).append("\n");
        }
        /**
         * 预定义格式: set caesar.xxx = xxx;
         */
        for(String statement:systemBuffer.toString().split(";")){
            if(StringUtils.isNotEmpty(statement.replaceAll("\\s",""))){
                String[] pairArray = statement
                        .replaceAll("\\s"," ")
                        .replaceAll("[S|s]+[E|e]+[T|t]+ ","")
                        .replaceAll("\\s","")
                        .replaceAll(";","")
                        .trim()
                        .split("=");
                if(!pairArray[0].startsWith("caesar")){
                    logger.warn(String.format("系统参数格式不符合预期:原始参数 => %s",statement.replaceAll("\\s","").trim()));
                    continue;
                }
                Pair pair = new Pair();
                pair.setKey(pairArray[0].trim());
                pair.setValue(pairArray[1].trim());
                systemParams.add(pair);
            }
        }

        // 2 Engine Parameters
        StringBuffer engineBuffer = new StringBuffer();
        for(String line:paramsModel.getEngineParams()){
            context.append(line).append("\n");
            engineBuffer.append(line).append("\n");
        }
        /**
         * 预定义格式: set xxx = xxx;
         */
        for(String statement:engineBuffer.toString().split(";")){
            if(StringUtils.isNotEmpty(statement.replaceAll("\\s",""))){
                String[] pairArray = statement
                        .replaceAll("\\s"," ")
                        .replaceAll("[S|s]+[E|e]+[T|t]+ ","")
                        .replaceAll("\\s","")
                        .replaceAll(";","")
                        .trim()
                        .split("=");
                Pair pair = new Pair();
                pair.setKey(pairArray[0].trim());
                pair.setValue(pairArray[1].trim());
                engineParams.add(pair);
            }
        }

        // 3 Custom Parameters
        StringBuffer customBuffer = new StringBuffer();
        for(String line:paramsModel.getCustomParams()){
            context.append(line).append("\n");
            customBuffer.append(line).append("\n");
        }

        /**
         * 预定义格式:
         *  type1: set xxx = xxx;
         *  type2:
         *      add jar xxx;
         *      create [temporary] function xxx;
         */
        for(String statement:customBuffer.toString().split(";")) {
            if (StringUtils.isNotEmpty(statement.replaceAll("\\s", ""))) {
                if(statement.replaceAll("\\s"," ").trim().toLowerCase().startsWith("set")){
                    String[] pairArray = statement
                            .replaceAll("\\s"," ")
                            .replaceAll("[S|s]+[E|e]+[T|t]+ ","")
                            .replaceAll("\\s","")
                            .replaceAll(";","")
                            .trim()
                            .split("=");
                    Pair pair = new Pair();
                    pair.setKey(pairArray[0].trim());
                    pair.setValue(pairArray[1].trim());
                    customParams.add(pair);
                }
                if(statement.replaceAll("\\s"," ").trim().toLowerCase().startsWith("create function") || statement.replaceAll("\\s"," ").trim().toLowerCase().startsWith("create temporary function")){
                    CustomFunctionParams customFunctionParams = new CustomFunctionParams();
                    customFunctionParams.setStatement(statement.replaceAll("\\s"," ").trim());
                    customParams.add(customFunctionParams);
                }
            }
        }

        content = context.deleteCharAt(context.length()-1).toString();

        paramsConfig.setContent(content);
        paramsConfig.setSystemParams(systemParams);
        paramsConfig.setEngineParams(engineParams);
        paramsConfig.setCustomParams(customParams);

        return paramsConfig;
    }


    private MetaContent parseMeta(List<String> metaList){

        MetaContent metaContent = new MetaContent();

        String content = "";
        String author = "";
        String groupName = "";
        String createTime = "";
        String taskName = "";
        EngineEnum engine = EngineEnum.HIVE;

        StringBuffer sb = new StringBuffer();
        for(String line: metaList){
            sb.append(line).append("\n");
            String[] tuple = line.replaceAll("\\s", "").split(":");
            if("作者".equals(tuple[0])){
                author = tuple[1];
            }

            if("创建时间".equals(tuple[0])){
                createTime = tuple[1];
            }

            if("任务属组".equals(tuple[0])){
                groupName = tuple[1];
            }

            if("任务名称".equals(tuple[0])){
                taskName = tuple[1];
            }

            if("执行引擎".equals(tuple[0])){
                engine = EngineEnum.fromEngine(tuple[1]);
            }
        }
        sb = sb.deleteCharAt(sb.length()-1);
        content = sb.toString();
        metaContent.setContent(content);
        metaContent.setAuthor(author);
        metaContent.setCreateTime(createTime);
        metaContent.setGroupName(groupName);
        metaContent.setTaskName(taskName);
        metaContent.setEngine(engine);

        return metaContent;
    }


}
