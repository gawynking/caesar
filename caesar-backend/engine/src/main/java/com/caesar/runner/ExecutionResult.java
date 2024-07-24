package com.caesar.runner;


import com.caesar.shell.ShellTask;

public class ExecutionResult<T> {
    private boolean success;
    private String message;

    private T data;

    public ExecutionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ExecutionResult(boolean success, String message,T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ExecutionResult(T data) {
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

