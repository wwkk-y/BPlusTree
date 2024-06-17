package com.BPlusTree.V2;

import com.BPlusTree.util.TestUtil;
import com.BPlusTree.util.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
//                bPlusTree.debug();
            } catch (NullPointerException e){
                ArrayList<BPlusTree.KVPair<String, String>> kvPairs = bPlusTree.toKVPairList();
                System.out.println("kvPairs:" + kvPairs);
                System.out.println("keys:" + keys);
                System.out.println("key: " + key);
                throw e;
            }

        }
        bPlusTree.debug(false);

        ArrayList<BPlusTree.KVPair<String, String>> kvPairs = bPlusTree.toKVPairList();
        List<String> keys2 = kvPairs.stream().map(BPlusTree.KVPair::getKey).collect(Collectors.toList());
        List<String> values2 = kvPairs.stream().map(BPlusTree.KVPair::getVal).collect(Collectors.toList());

        if(!TestUtil.isSorted(keys2)){
            System.out.println("keys2:" + keys2);
            throw new RuntimeException("未排序");
        }

        if(!TestUtil.isArrayElementEqual(keys, keys2)){
            System.out.println(keys);
            System.out.println(keys2);
            throw new RuntimeException("不相等");
        }

        if(!TestUtil.isArrayElementEqual(values, values2)){
            System.out.println(values);
            System.out.println(values2);
            throw new RuntimeException("不相等");
        }

        System.out.println("success");
        return bPlusTree;
    }

    public static void testInsertMain() {
        // 测试一层插入
        for (int i = 0; i < 10; i++) {
            int degree = RandomGenerator.generateRandomNumber(1000, 10000);
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
    }

    public static void testSelect(int degree, boolean unique, int size){
        BPlusTree<String, String> bPlusTree = testInsert(degree, unique, size);
        ArrayList<BPlusTree.KVPair<String, String>> kvPairs = bPlusTree.toKVPairList();
        kvPairs.forEach(kv -> {
            if(bPlusTree.select(kv.getKey()).compareTo(kv.getVal()) != 0){
                throw new RuntimeException("查找错误");
            }
        });
        System.out.println("success");
    }

    public static void testSelectMain(){
        for (int i = 3; i < 1000; i *= 2) {
            for (int j = i - 1;  j < 100000; j *= 3) {
                System.out.printf("degree: %d, size: %d \n", i, j);
                testSelect(i, true, j);
                TestUtil.hr();
            }
        }
    }

    public static void testUpdate(int degree, boolean unique, int size){
        BPlusTree<String, String> bPlusTree = testInsert(degree, unique, size);
        ArrayList<BPlusTree.KVPair<String, String>> kvPairs = bPlusTree.toKVPairList();
        kvPairs.forEach(kv -> {
            bPlusTree.update(kv.getKey(), "1");
            if (bPlusTree.select(kv.getKey()).compareTo("1") != 0) {
                throw new RuntimeException("更新错误");
            }
        });

        System.out.println("success");
    }

    public static void testUpdateMain(){
        for (int i = 3; i < 1000; i *= 2) {
            for (int j = i - 1;  j < 100000; j *= 3) {
                System.out.printf("degree: %d, size: %d \n", i, j);
                testUpdate(i, true, j);
                TestUtil.hr();
            }
        }
    }

    public static void main(String[] args) {
        testInsertMain(); // 测试插入
        testSelectMain(); // 测试查找
        testUpdateMain(); // 测试更新
    }
}
