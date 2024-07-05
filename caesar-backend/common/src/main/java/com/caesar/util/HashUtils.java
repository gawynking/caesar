package com.caesar.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static String getMD5Hash(String input) {
        try {
            // 创建MessageDigest实例，指定算法为MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算MD5哈希值
            byte[] hashBytes = md.digest(input.getBytes());
            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }




}
