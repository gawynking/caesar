package com.caesar.enums;

public enum ParamsCategoryEnum {

    OTHER(0,"Other"),

    SYSTEM(1, "System"),
    ENGINE(2, "Engine"),
    CUSTOM(3, "Custom")
    ;


    private final int tag;
    private final String category;

    ParamsCategoryEnum(int tag, String category) {
        this.tag = tag;
        this.category = category;
    }

    public int getTag() {
        return tag;
    }

    public String getCategory() {
        return category;
    }

    public static ParamsCategoryEnum fromTag(int tag) {
        for (ParamsCategoryEnum engine : values()) {
            if (engine.tag == tag) {
                return engine;
            }
        }
        throw new IllegalArgumentException("Unknown tag: " + tag);
    }

    public static ParamsCategoryEnum fromCategory(String category) {
        for (ParamsCategoryEnum engine : values()) {
            if (engine.category.equalsIgnoreCase(category)) {
                return engine;
            }
        }
        throw new IllegalArgumentException("Unknown Category: " + category);
    }

    @Override
    public String toString() {
        return "ExecutionEngine{" +
                "Tag=" + tag +
                ", Category='" + category + '\'' +
                '}';
    }
}
