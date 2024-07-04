package com.caesar.enums;

public enum TemplateSectionEnum {

    META("meta","Meta"),
    PARAMS("params","Part One"),
    SCHEMA("schema","Part Two"),
    ETL("etl","Part Three"),
    OTHER("other","Other");

    private final String sectionTag;
    private final String sectionName;

    TemplateSectionEnum(String sectionTag, String sectionName) {
        this.sectionTag = sectionTag;
        this.sectionName = sectionName;
    }

    public String getSectionTag() {
        return sectionTag;
    }

    public String getSectionName() {
        return sectionName;
    }

    public static TemplateSectionEnum fromSectionTag(String sectionTag) {
        for (TemplateSectionEnum section : values()) {
            if (section.sectionTag.equalsIgnoreCase(sectionTag)) {
                return section;
            }
        }
        throw new IllegalArgumentException("Unknown section: " + sectionTag);
    }

    public static TemplateSectionEnum fromSectionName(String sectionName) {
        for (TemplateSectionEnum section : values()) {
            if (section.sectionName.equalsIgnoreCase(sectionName)) {
                return section;
            }
        }
        throw new IllegalArgumentException("Unknown section: " + sectionName);
    }
}
