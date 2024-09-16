package com.caesar.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum PriorityEnum {

    /**
     * 1 lower priority
     * 2 medium priority
     * 3 higher priority
     */
    LOW(1, "lower"),
    MEDIUM(2, "medium"),
    HIGH(3, "higher");

    private final int code;
    private final String desc;

    // 构造函数
    PriorityEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return desc;
    }

    // 用于从code获取枚举值的静态方法
    public static PriorityEnum fromCode(int code) {
        for (PriorityEnum priority : values()) {
            if (priority.getCode() == code) {
                return priority;
            }
        }
        return null; // 或者抛出异常，具体取决于业务逻辑
    }
}