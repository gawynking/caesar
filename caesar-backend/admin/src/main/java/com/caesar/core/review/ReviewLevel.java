package com.caesar.core.review;

public enum ReviewLevel {

    INITIAL(1, "initial"),
    SECONDARY(2, "secondary"),
    FINAL(3, "final");

    private final int key;
    private final String value;

    ReviewLevel(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static ReviewLevel fromKey(int key) {
        for (ReviewLevel level : ReviewLevel.values()) {
            if (level.getKey() == key) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }
}
