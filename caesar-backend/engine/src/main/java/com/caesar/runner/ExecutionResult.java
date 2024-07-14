package com.caesar.runner;


public class ExecutionResult {
    private boolean success;
    private String message;

    private Object data;

    // 构造器
    public ExecutionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getter 和 Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

