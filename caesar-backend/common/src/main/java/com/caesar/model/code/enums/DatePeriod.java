package com.caesar.model.code.enums;

public enum DatePeriod {
    DAY("day"),
    WEEK("week"),
    MONTH("month");

    private final String key;

    // 构造函数
    DatePeriod(String key) {
        this.key = key;
    }

    // 获取key
    public String getKey() {
        return key;
    }

    // 从key获取枚举实例的静态方法
    public static DatePeriod fromKey(String key) {
        for (DatePeriod datePeriod : values()) {
            if (datePeriod.getKey().equals(key)) {
                return datePeriod;
            }
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }
}