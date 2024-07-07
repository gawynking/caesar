package com.caesar.enums;

public enum EngineEnum {

    HIVE(1, "Hive"),
    SPARK(2, "Spark"),
    FLINK(3, "Flink"),
    DORIS(4, "Doris"),
    HUDI(5, "Hudi"),
    PAIMON(6, "Paimon"),
    HBASE(7, "Hbase"),
    KAFKA(8, "Kafka"),
    STARROCKS(9, "StarRocks");

    private final int tag;
    private final String engine;

    EngineEnum(int value, String label) {
        this.tag = value;
        this.engine = label;
    }

    public int getTag() {
        return tag;
    }

    public String getEngine() {
        return engine;
    }

    public static EngineEnum fromTag(int tag) {
        for (EngineEnum engine : values()) {
            if (engine.tag == tag) {
                return engine;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + tag);
    }

    public static EngineEnum fromEngine(String label) {
        for (EngineEnum engine : values()) {
            if (engine.engine.equalsIgnoreCase(label)) {
                return engine;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }

    @Override
    public String toString() {
        return "ExecutionEngine{" +
                "Tag=" + tag +
                ", Engine='" + engine + '\'' +
                '}';
    }
}
