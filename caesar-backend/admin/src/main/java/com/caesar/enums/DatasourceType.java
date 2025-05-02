package com.caesar.enums;


public enum DatasourceType {

    TEST(1, "测试"),
    PRE_PRODUCTION(2, "预发"),
    PRODUCTION(3, "生产");

    private final int value;
    private final String description;

    DatasourceType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return value + " - " + description;
    }

    public static DatasourceType fromValue(int value) {
        for (DatasourceType type : DatasourceType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
