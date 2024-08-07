package com.caesar.enums;


public enum SchedulerEnum {
    HERA("hera", "Hera Scheduler"),
    DOLPHINSCHEDULER("dolphin", "DolphinScheduler");

    private final String key;
    private final String description;


    SchedulerEnum(String key, String description) {
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

    public static SchedulerEnum fromKey(String key) {
        for (SchedulerEnum type : SchedulerEnum.values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown key: " + key);
    }
}
