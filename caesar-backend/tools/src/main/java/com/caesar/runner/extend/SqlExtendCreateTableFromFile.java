package com.caesar.runner.extend;

import lombok.Data;

/**
 * 自定义SQL语句
 */
public class SqlExtendCreateTableFromFile implements ExtendSql<SqlExtendCreateTableFromFile.ExtendSqlModel>{


    @Override
    public Boolean validateSqlSyntax(String sql){

        String[] elements = sql.replaceAll("\\s", " ").replace(";", "").trim().split(" ");
        if(null != elements && elements.length == 7){
            StringBuilder sqlPrefix = new StringBuilder();
            StringBuilder keyWords = new StringBuilder();
            for(int i=0; i<3; i++){
                sqlPrefix.append(elements[i]).append(" ");
            }
            for(int i=4; i<6; i++){
                keyWords.append(elements[i]).append(" ");
            }
            if(sqlPrefix.toString().trim().toLowerCase().equals("create temporary table") && keyWords.toString().trim().toLowerCase().equals("use files")){
                return true;
            }
        }

        return false;
    }


    /**
     * 功能：通过扩展SQL语句将文件解析成表
     * 语法: create temporary table tbl_text use files '/Users/chavinking/test/1.txt';
     */
    @Override
    public ExtendSqlModel parseMeta(String extendSql){

        String[] elements = extendSql.replaceAll("\\s", " ").replace(";", "").trim().split(" ");
        if(validateSqlSyntax(extendSql)){
            String tableName = elements[3].trim();
            String fileElement = elements[6].trim();
            if(fileElement.startsWith("'") && fileElement.endsWith("'")){
                char[] chars = fileElement.toCharArray();
                StringBuilder filePathBuffer = new StringBuilder();
                for(int i=1; i<chars.length-1; i++){
                    filePathBuffer.append(chars[i]);
                }
                String filePath = filePathBuffer.toString();
                String[] fileElements = filePath.split("\\.");
                FileTypeEnum fileTypeEnum = FileTypeEnum.TXT;
                if(fileElements.length >= 2){
                    fileTypeEnum = FileTypeEnum.fromStringContent(fileElements[fileElements.length - 1]);
                }
                ExtendSqlModel result = new ExtendSqlModel();
                result.setTableName(tableName);
                result.setFilePath(filePath);
                result.setFileType(fileTypeEnum);
                return result;
            }
        }

        return null;
    }



    @Data
    public static class ExtendSqlModel {
        String tableName;
        String filePath;
        FileTypeEnum fileType;
    }

    public static enum FileTypeEnum{
        JSON, CSV, TXT;

        // 从字符串推断文件类型的方法
        public static FileTypeEnum fromStringContent(String content) {
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content must not be null or empty.");
            }

            // 检查是否为JSON格式
            if (content.toLowerCase().equals("json")) {
                return JSON;
            } else if (content.toLowerCase().equals("csv")) {
                return CSV;
            } else {
                return TXT;
            }
        }
    }

}
