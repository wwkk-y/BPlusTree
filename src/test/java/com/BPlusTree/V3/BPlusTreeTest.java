package com.BPlusTree.V3;

import com.BPlusTree.util.RandomGenerator;
import com.BPlusTree.util.TestUtil;

import java.util.ArrayList;

public class BPlusTreeTest {

    /**
     * 测试插入数据
     * @param degree 阶数
     * @param unique 是否唯一索引
     * @param size 插入数据规模
     * @return 生成的 B+ 树
     */
    public static BPlusTree<String, String> testInsert(int degree, boolean unique, int size){
        BPlusTree<String, String> bPlusTree = new BPlusTree<>(unique, degree);

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
//            Integer key = RandomGenerator.generateRandomNumber(1, Integer.MAX_VALUE);
            String key = RandomGenerator.generateRandomString(10);
            String value = RandomGenerator.generateRandomString(5);
            keys.add(key);
            values.add(value);

            try {
                bPlusTree.insert(key, value);
            } catch (NullPointerException e){
                bPlusTree.check(true, keys, values, false);
                System.out.println("keys:" + keys);
                System.out.println("key: " + key);
                throw e;
            }

        }
        bPlusTree.check(false, keys, values);

        System.out.println("testInsert success");
        return bPlusTree;
    }

    public static void testInsertMain() {
        // 测试一层插入
        for (int i = 0; i < 10; i++) {
            int degree = RandomGenerator.generateRandomNumber(1, 10);
            for (int j = 0; j < 10; j++) {
                int size = RandomGenerator.generateRandomNumber(1, degree);
                System.out.printf("degree: %d, size: %d \n", degree, size);
                testInsert(degree, true, size);
            }
        }
        // 测试多层插入
        for (int i = 3; i < 1000; i *= 2) {
            for (int j = i - 1;  j < 100000; j *= 3) {
                System.out.printf("degree: %d, size: %d \n", i, j);
                testInsert(i, true, j);
                TestUtil.hr();
            }
        }
        System.out.println("testInsertMain success");
    }

    public static void main(String[] args) {
        testInsertMain();
    }
}
