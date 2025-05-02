package com.caesar.shell.script;

public abstract class ScriptOperator {

    protected void syncExecuteScript(String tmpSqlFilePath,String tmpShellFilePath){

        if(null != tmpSqlFilePath) syncSqlScript(tmpSqlFilePath);
        if(null != tmpShellFilePath) syncShellScript(tmpShellFilePath);
    }

    abstract Boolean syncSqlScript(String tmpSqlFilePath);
    abstract Boolean syncShellScript(String tmpShellFilePath);

}
