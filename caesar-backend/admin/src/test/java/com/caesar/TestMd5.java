package com.caesar;

import com.alibaba.fastjson.JSONObject;
import com.caesar.util.HashUtils;
import com.caesar.util.JSONUtils;

public class TestMd5 {

    public static void main(String[] args) {

        String taskScript = "sadfsaaaaaa";
        String md5Hash = HashUtils.getMD5Hash(taskScript);
        System.out.println(md5Hash);


        String str = "{\"task_type\":1}";
        JSONObject jsonObjectFromString = JSONUtils.getJSONObjectFromString(str);
        System.out.println(jsonObjectFromString);
        System.out.println(jsonObjectFromString.getInteger("task_type"));

    }
}
