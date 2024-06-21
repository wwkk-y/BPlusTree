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
        if(unique){
            // 唯一索引
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
        }
        if(!unique){
            // 测试非唯一索引, 插入多条重复数据
            int keySetSize = size / 100 + 1;
            for (int i = 0; i < keySetSize; i++) {
//            Integer key = RandomGenerator.generateRandomNumber(1, Integer.MAX_VALUE);
                String key = RandomGenerator.generateRandomString(10);
                String value = RandomGenerator.generateRandomString(5);
                keys.add(key);
                values.add(value);
            }
            for (int i = keySetSize; i < size; i++) {
                keys.add(keys.get(i - keySetSize));
                values.add(values.get(i - keySetSize));
            }

            for (int i = 0; i < keys.size(); i++) {
                try {
                    bPlusTree.insert(keys.get(i), values.get(i));
                } catch (NullPointerException e){
                    bPlusTree.check(true, keys, values, false);
                    System.out.println("keys:" + keys);
                    System.out.println("key: " + keys.get(i));
                    throw e;
                }
            }

        }
        bPlusTree.check(false, keys, values);
        System.out.println("test insert success");

        // 测试其他的
        testSelect(bPlusTree, keys, values);
        testUpdate(bPlusTree, keys, values);
        testDelete(bPlusTree, keys, values);

        TestUtil.hr();
    }

    /**
     * 测试删除
     */
    public static void testDelete(BPlusTree<String, String> bPlusTree, ArrayList<String> keys, ArrayList<String> values){
//        bPlusTree.check(true);
        for (int i = 0; i < keys.size(); i++) {
//            System.out.println("\n");
//            System.out.printf("delete: %s\n", keys.get(i));
            bPlusTree.delete(keys.get(i));
//            bPlusTree.check(true);
        }
        bPlusTree.check(false);
        System.out.println("test delete success");
        TestUtil.hr();
    }

    /**
     * 测试查找
     */
    public static void testSelect(BPlusTree<String, String> bPlusTree, ArrayList<String> keys, ArrayList<String> values){
        for (int i = 0; i < keys.size(); i++) {
            ArrayList<String> valueList = bPlusTree.select(keys.get(i));
            if(bPlusTree.unique){
                if(valueList.size() != 1 || valueList.get(0) != values.get(i)){
                    System.out.println(keys);
                    System.out.println(values);
                    System.out.println(keys.get(i));
                    System.out.println(valueList);
                    throw new RuntimeException("查找错误");
                }
            } else {
                if(valueList.size() == 0){
                    System.out.println(keys);
                    System.out.println(values);
                    System.out.println(keys.get(i));
                    System.out.println(valueList);
                    throw new RuntimeException("查找错误");
                }
                for (String value : valueList) {
                    if(!value.equals(values.get(i))){
                        System.out.println(keys);
                        System.out.println(values);
                        System.out.println(keys.get(i));
                        System.out.println(valueList);
                        throw new RuntimeException("查找错误");
                    }
                }
            }
        }
        for (int i = 0; i < 100; i++) {
            ArrayList<String> valueList = bPlusTree.select(RandomGenerator.generateRandomString(5));
            if(valueList.size() != 0){
                System.out.println(valueList);
                throw new RuntimeException("查找错误");
            }
        }
        ArrayList<String> valueList = bPlusTree.select(null);
        if(valueList.size() != 0){
            System.out.println(valueList);
            throw new RuntimeException("查找错误");
        }
        System.out.println("test select success");
    }

    /**
     * 测试更新
     */
    public static void testUpdate(BPlusTree<String, String> bPlusTree, ArrayList<String> keys, ArrayList<String> values){
        for (int i = 0; i < keys.size(); i++) {
            values.set(i, RandomGenerator.generateRandomString(5));
            bPlusTree.update(keys.get(i), values.get(i));
            ArrayList<String> valueList = bPlusTree.select(keys.get(i));
            if(valueList.size() == 0 || valueList.get(0) != values.get(i)){
                System.out.println(keys);
                System.out.println(values);
                System.out.println(keys.get(i));
                System.out.println(valueList);
                throw new RuntimeException("更新错误");
            }
        }
        System.out.println("test update success");
    }

    public static void testMain(String[] args) {
        // 测试一层
        for (int i = 0; i < 10; i++) {
            int degree = RandomGenerator.generateRandomNumber(1, 10);
            for (int j = 0; j < 10; j++) {
                int size = RandomGenerator.generateRandomNumber(1, degree);
                System.out.printf("degree: %d, size: %d \n", degree, size);
                System.out.println("unique");
                test(degree, true, size);
                System.out.println("not unique");
                test(degree, false, size);
                TestUtil.hr(100);
            }
        }
        // 测试多层
        for (int i = 3; i < 1000; i *= 2) {
            for (int j = i - 1;  j < 100000; j *= 3) {
                System.out.printf("degree: %d, size: %d \n", i, j);
                TestUtil.hr();
                System.out.println("unique");
                test(i, true, j);
                System.out.println("not unique");
                test(i, false, j);
                TestUtil.hr();
                TestUtil.hr(100);
            }
        }
        System.out.println("done!");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            testMain(args);
        }
    }
}
