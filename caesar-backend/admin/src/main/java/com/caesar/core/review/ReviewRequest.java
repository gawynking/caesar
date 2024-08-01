package com.caesar.core.review;


public class ReviewRequest {

    private int taskId;
    private String taskName;
    private int version;
    private String submitUsername;
    private String codeDesc;


    public ReviewRequest(int taskId, String taskName, int version, String submitUsername, String codeDesc) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.version = version;
        this.submitUsername = submitUsername;
        this.codeDesc = codeDesc;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSubmitUsername() {
        return submitUsername;
    }

    public void setSubmitUsername(String submitUsername) {
        this.submitUsername = submitUsername;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

}

