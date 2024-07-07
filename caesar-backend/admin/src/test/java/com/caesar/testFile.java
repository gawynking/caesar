package com.caesar;

import com.caesar.util.IOUtils;

public class testFile {

    public static void main(String[] args) {

        String temp = IOUtils.readFileToBuffer("/Users/chavinking/github/caesar-complete/caesar-backend/admin/src/main/resources/TaskTemplate.code");
        System.out.println(temp);


    }
}
