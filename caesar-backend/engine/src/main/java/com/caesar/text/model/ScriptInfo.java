package com.caesar.text.model;

import lombok.Data;

import java.util.List;

@Data
public class ScriptInfo {

    List<String> schedulerCluster;

    String testSqlFile;
    String testScriptFile;
    String testScript;

    String prodSqlFile;
    String prodScriptFile;
    String prodScript;

}
