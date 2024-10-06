package com.caesar.enums;

public enum EnvironmentEnum {

    TEST("test", "Test Environment"),
    STAGING("staging", "Staging Environment"), // 不建议使用
    PRODUCTION("production", "Production Environment");

    private final String key;
    private final String value;

    EnvironmentEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Environment{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static EnvironmentEnum fromKey(String key) {
        for (EnvironmentEnum env : EnvironmentEnum.values()) {
            if (env.getKey().equalsIgnoreCase(key)) {
                return env;
            }
        }
        throw new IllegalArgumentException("No enum constant with key " + key);
    }

}
