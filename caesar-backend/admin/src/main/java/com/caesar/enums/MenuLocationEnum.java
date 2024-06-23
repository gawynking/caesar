package com.caesar.enums;

public enum MenuLocationEnum {
    // 头部菜单栏
    HEADER(1, "头部菜单栏"),
    // 侧边菜单栏
    SIDEBAR(2, "侧边菜单栏");

    private final int code;
    private final String description;

    // 构造方法
    MenuLocationEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取代码
    public int getCode() {
        return code;
    }

    // 获取描述
    public String getDescription() {
        return description;
    }

    // 根据代码获取枚举
    public static MenuLocationEnum fromCode(int code) {
        for (MenuLocationEnum location : MenuLocationEnum.values()) {
            if (location.code == code) {
                return location;
            }
        }
        throw new IllegalArgumentException("未知的菜单位置代码: " + code);
    }

    @Override
    public String toString() {
        return "MenuLocationEnum{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}
