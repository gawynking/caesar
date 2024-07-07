package com.caesar.model.code;


import com.caesar.enums.EngineEnum;
import com.caesar.enums.TemplateSectionEnum;
import com.caesar.model.code.model.*;

import java.util.ArrayList;
import java.util.List;


public class TaskContentParser {

    private TaskContentModel taskContentModel;

    TaskContentParser(String content){
        this.taskContentModel = parseScriptContent(content);
    }

    private String getString(List<String> list){
        StringBuffer stringBuffer = new StringBuffer();
        for(String line :list){
            stringBuffer.append(line).append("\n");
        }
        return stringBuffer.deleteCharAt(stringBuffer.length()-1).toString();
    }

    private String generateExecuteScript(){
        StringBuffer execScript = new StringBuffer();
        ParamsConfig paramsConfig = this.taskContentModel.getParamsConfig();
        SchemaDefine schemaDefine = this.taskContentModel.getSchemaDefine();
        EtlProcess etlProcess = this.taskContentModel.getEtlProcess();

        execScript.append(getString(paramsConfig.getEngineParams())).append("\n");
        execScript.append(getString(paramsConfig.getCustomParams())).append("\n");
        execScript.append(schemaDefine.getContent()).append("\n");
        execScript.append(etlProcess.getContent());

        return execScript.toString();
    }


    // 解析模板内容
    private TaskContentModel parseScriptContent(String content) {

        TaskContentModel taskContentModel = new TaskContentModel();

        List<String> meta = new ArrayList<>();
        List<String> params = new ArrayList();
        List<String> schema = new ArrayList();
        List<String> etl = new ArrayList();

        TemplateSectionEnum flag = TemplateSectionEnum.OTHER;
        String[] lines = content.split("\n");
        for(String line :lines){
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

            if(TemplateSectionEnum.META == flag && !line.replaceAll("\\s","").startsWith("#")){
                meta.add(line);
            } else if(TemplateSectionEnum.PARAMS == flag && !line.replaceAll("\\s","").startsWith("#")){
                params.add(line);
            } else if(TemplateSectionEnum.SCHEMA == flag && !line.replaceAll("\\s","").startsWith("#")){
                schema.add(line);
            } else if(TemplateSectionEnum.ETL == flag && !line.replaceAll("\\s","").startsWith("#")){
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

    private ParamsConfig parseParams(List<String> paramsList){
        ParamsConfig paramsConfig = new ParamsConfig();

        String content;
        List<String> systemParams = new ArrayList<>();
        List<String> engineParams = new ArrayList<>();
        List<String> customParams = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        int flag = -1;
        for(String line:paramsList){
            sb.append(line).append("\n");
            if(line.replaceAll("\\s","").startsWith("--系统参数")){
                flag = 1;
                continue;
            }
            if(line.replaceAll("\\s","").startsWith("--引擎参数")){
                flag = 2;
                continue;
            }
            if(line.replaceAll("\\s","").startsWith("--自定义参数")){
                flag = 3;
                continue;
            }

            if(flag==1){
                systemParams.add(line);
            }
            if(flag==2){
                engineParams.add(line);
            }
            if(flag==3){
                customParams.add(line);
            }
        }
        content = sb.deleteCharAt(sb.length()-1).toString();

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
