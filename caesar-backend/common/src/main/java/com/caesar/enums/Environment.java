package com.caesar.enums;

public enum Environment {

    TEST("test", "Test Environment"),
    STAGING("staging", "Staging Environment"),
    PRODUCTION("production", "Production Environment");

    private final String key;
    private final String value;

    Environment(String key, String value) {
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

    public static Environment fromKey(String key) {
        for (Environment env : Environment.values()) {
            if (env.getKey().equalsIgnoreCase(key)) {
                return env;
            }
        }
        throw new IllegalArgumentException("No enum constant with key " + key);
    }

}
