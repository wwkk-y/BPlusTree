package com.BPlusTree.util;

import java.util.Random;

public class RandomGenerator {

    // 生成随机数的函数，范围为[min, max]
    public static int generateRandomNumber(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min不能大于max");
        }
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    // 生成随机字符串的函数，长度为length
    public static String generateRandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
