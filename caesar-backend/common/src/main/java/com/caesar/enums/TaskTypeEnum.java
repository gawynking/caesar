package com.caesar.enums;

public enum TaskTypeEnum {
    SHELL("shell", "Shell Command Task"),
    JDBC("jdbc", "JDBC Database Task"),
    SPARK("spark", "Spark Job Task"),
    FLINK("flink", "Flink Job Task");


    private final String key;
    private final String description;

    TaskTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return key + " (" + description + ")";
    }

    public static TaskTypeEnum fromKey(String key) {
        for (TaskTypeEnum type : TaskTypeEnum.values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown key: " + key);
    }
}
