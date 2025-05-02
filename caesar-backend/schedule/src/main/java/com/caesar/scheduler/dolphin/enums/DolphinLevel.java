package com.caesar.scheduler.dolphin.enums;

public enum DolphinLevel {

    // 项目级别调度
    PROJECT("project", "Project Level"),
    // 工作流级别调度
    WORKFLOW("workflow", "Workflow Level");

    private String code;
    private String description;

    // 构造函数，为每个枚举常量设置码值和描述信息
    DolphinLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取码值的方法
    public String getCode() {
        return code;
    }

    // 获取描述信息的方法
    public String getDescription() {
        return description;
    }

    // 静态方法，用于根据码值查找对应的枚举常量
    public static DolphinLevel getByCode(String code) {
        for (DolphinLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}