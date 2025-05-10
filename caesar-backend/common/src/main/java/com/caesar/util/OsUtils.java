package com.caesar.util;

public class OsUtils {


    public static Boolean isWindowsOs(){
        return System.getProperty("os.name").contains("Windows");
    }

    public static Boolean isMacOs(){
        return System.getProperty("os.name").contains("Mac");
    }

    public static Boolean isTest(){
        return isWindowsOs() || isMacOs();
    }


}
