package com.caesar.enums;


public enum MenuType {

    STATIC_MENU(1),
    NAMED_MENU(2);

    private final int value;

    MenuType(Integer value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MenuType fromValue(int value) {
        for (MenuType status : MenuType.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }


}
