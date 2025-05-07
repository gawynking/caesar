package com.caesar.text.model;

import lombok.Data;

import java.util.List;

@Data
public class ScriptInfo {

    List<String> schedulerCluster;

    String testSqlFile;
    String testScript;

    String prodSqlFile;
    String prodScript;

}
