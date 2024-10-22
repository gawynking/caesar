package com.caesar;

public class SplitTest {

    public static void main(String[] args) {

        String str = "caesar1__caesar2";
        String[] s = str.split("__");
        System.out.println(s[0]);
        System.out.println(s[1]);

    }
}
