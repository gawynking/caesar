package com.caesar.enums;

public enum SchedulingPeriod {

    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month");

    private final String period;

    SchedulingPeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public static SchedulingPeriod fromString(String text) {
        for (SchedulingPeriod b : SchedulingPeriod.values()) {
            if (text.equalsIgnoreCase(b.period)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}