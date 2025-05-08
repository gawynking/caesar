package com.caesar.text.model;

import com.caesar.enums.EnvironmentEnum;
import lombok.Data;

import java.util.List;

@Data
public class ScriptInfo {

    String fullTaskName;

    List<String> schedulerCluster;

    String testSqlFile;
    String testScriptFile;
    String testScript;

    String prodSqlFile;
    String prodScriptFile;
    String prodScript;

    String executeUser;
    EnvironmentEnum environment;

}
