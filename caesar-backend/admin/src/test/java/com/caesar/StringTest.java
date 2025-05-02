package com.caesar;

import org.junit.jupiter.api.Test;

public class StringTest {

    @Test
    public void test01(){
        String str = "caesar___gawyn";
        if(str.contains("___")){
            System.out.println(str.split("___")[0]);
            System.out.println(str.split("___")[1]);
        }
    }

}
