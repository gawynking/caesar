package com.caesar.enums;

public enum EngineEnum {

    NONE(1,"None"),

    HIVE(101, "Hive"),
    SPARK(102, "Spark"),
    FLINK(103, "Flink"),
    KAFKA(104, "Kafka"),

    HUDI(201, "Hudi"),
    PAIMON(202, "Paimon"),

    DORIS(301, "Doris"),
    STARROCKS(302, "StarRocks"),

    MYSQL(401,"MySQL"),

    HBASE(501, "Hbase"),

    REDIS(601,"Redis")
    ;

    private final int tag;
    private final String engine;

    EngineEnum(int tag, String label) {
        this.tag = tag;
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
