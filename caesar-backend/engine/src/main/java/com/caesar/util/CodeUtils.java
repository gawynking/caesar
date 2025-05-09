package com.caesar.util;

public class CodeUtils {


    /**
     * 向执行SQL代码中插入Hive引擎参数
     *
     * @param code
     * @param engineParams
     * @return
     */
    public static String addEngineParamsForHive(String code, String engineParams){

        StringBuffer finalSqlCode = new StringBuffer();
        boolean flag = false;
        String[] lines = code.split("\n");
        for(String line: lines){
            if(line.trim().startsWith("--") && line.contains("Version")){
                finalSqlCode
                        .append(line)
                        .append("\n\n\n")
                        .append(engineParams)
                        .append("\n");
            }else{
                finalSqlCode
                        .append(line)
                        .append("\n");
            }
        }

        return finalSqlCode.toString();
    }


    /**
     * 向执行SQL代码中插入SparkSQL参数
     *
     * @param code
     * @param sqlParams
     * @return
     */
    public static String addEngineParamsForSpark(String code, String sqlParams){

        StringBuffer finalSqlCode = new StringBuffer();
        boolean flag = false;
        String[] lines = code.split("\n");
        for(String line: lines){
            if(line.trim().startsWith("--") && line.contains("Version")){
                finalSqlCode
                        .append(line)
                        .append("\n\n\n")
                        .append(sqlParams)
                        .append("\n");
            }else{
                finalSqlCode
                        .append(line)
                        .append("\n");
            }
        }

        return finalSqlCode.toString();
    }


    public static String dosToUnix(String script) {
        return script.replaceAll("\r\n", "\n");
    }

}
