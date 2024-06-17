package com.BPlusTree.V3;

import com.BPlusTree.util.RandomGenerator;
import com.BPlusTree.util.TestUtil;

import java.util.ArrayList;

public class BPlusTreeTest {

    /**
     * 测试
     * @param degree 阶数
     * @param unique 是否唯一索引
     * @param size 插入数据规模
     */
    public static void test(int degree, boolean unique, int size){
        BPlusTree<String, String> bPlusTree = new BPlusTree<>(unique, degree);

        // 测试插入
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
        System.out.println("test insert success");

        // 测试其他的
        testSelect(bPlusTree, keys, values);

        TestUtil.hr(100);
    }

    /**
     * 测试查找
     */
    public static void testSelect(BPlusTree<String, String> bPlusTree, ArrayList<String> keys, ArrayList<String> values){
        for (int i = 0; i < keys.size(); i++) {
            ArrayList<String> valueList = bPlusTree.select(keys.get(i));
            if(valueList.size() == 0 || valueList.get(0) != values.get(i)){
                System.out.println(keys);
                System.out.println(values);
                System.out.println(keys.get(i));
                System.out.println(valueList);
                throw new RuntimeException("查找错误");
            }
        }
        System.out.println("test select success");
    }

    public static void main(String[] args) {
        // 测试一层插入
        for (int i = 0; i < 10; i++) {
            int degree = RandomGenerator.generateRandomNumber(1, 10);
            for (int j = 0; j < 10; j++) {
                int size = RandomGenerator.generateRandomNumber(1, degree);
                System.out.printf("degree: %d, size: %d \n", degree, size);
                test(degree, true, size);
            }
        }
        // 测试多层插入
        for (int i = 3; i < 1000; i *= 2) {
            for (int j = i - 1;  j < 100000; j *= 3) {
                System.out.printf("degree: %d, size: %d \n", i, j);
                test(i, true, j);
                TestUtil.hr();
            }
        }
        System.out.println("done!");
    }
}
