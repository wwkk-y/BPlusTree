package com.BPlusTree.V1;


import com.BPlusTree.util.RandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.BPlusTree.util.TestUtil.*;

public class BPlusTreeTest {

    /**
     * 测试已经有数据的 B+ 树, B+ 树值类型统一用String
     * @param <T> B+ 树主键类型
     */
    public static <T extends Comparable<T>> void testBPlusTree(BPlusTree<T> bPlusTree, Map<T, Object> map){
        // 测试顺序(是否排序)
        HashMap<String, Object> out = bPlusTree.out(false);
        ArrayList<T> outKeys = (ArrayList<T>) out.get("keys");
        ArrayList<Object> outValues = (ArrayList<Object>) out.get("values");
        if (!isSorted(outKeys)) {
            System.out.println(outKeys);
            throw new RuntimeException("sort error");
        }
        // 测试数据size是否对
        if(outKeys.size() != map.size()){
            System.out.println(outKeys);
            throw new RuntimeException("size error");
        }
        if(outValues.size() != map.size()){
            System.out.println(outValues);
            throw new RuntimeException("size error");
        }
        // 测试搜索
        for (T searchKey : map.keySet()) {
            BPlusTreeNodePage<T> nodePage = bPlusTree.searchPage(searchKey);
            String val = (String) bPlusTree.searchValue(searchKey);

            if(!nodePage.getKeys().contains(searchKey)){
                System.out.println("searchKey: " + searchKey);
                System.out.println(nodePage);
                System.out.println(val);
                throw new RuntimeException("searchPage error");
            }

            if(!val.equals(map.get(searchKey))){
                System.out.println("searchKey: " + searchKey);
                System.out.println(nodePage);
                System.out.println(val);
                throw new RuntimeException("searchValue error");
            }
        }
    }

    /**
     * 测试 key 为 int 类型时的 B+ 树, 随机生成key
     * @param degree B+ 树阶数
     * @param dataSize 插入数据量
     */
    public static void testRandomInt(int degree, int dataSize){
        BPlusTree<Integer> bPlusTree = new BPlusTree<>(degree);

        // 插入数据测试
        Map<Integer, Object> map = new HashMap<>();
        for (int i = 1; i <= dataSize; i++) {
            Integer key = RandomGenerator.generateRandomNumber(1, Integer.MAX_VALUE);
            String value = RandomGenerator.generateRandomString(10);
            map.put(key, value);
            try {
                bPlusTree.insert(key, value);
            } catch (Exception e){
                e.printStackTrace();
                bPlusTree.out(true);
                throw new UniqueKeyException("treeInsert error: " + i);
            }

        }

        // 测试其他
        testBPlusTree(bPlusTree, map);

        System.out.printf("testRandomInt{degree=%d, dataSize=%d} success\n", degree, dataSize);
        hr(100);
    }

    /**
     * 测试 key 为 int 类型时的 B+ 树, key为正序排序数据
     * @param degree B+ 树阶数
     * @param dataSize 插入数据量
     */
    public static void testSortedInt(int degree, int dataSize){
        BPlusTree<Integer> bPlusTree = new BPlusTree<>(degree);

        // 插入数据测试
        Map<Integer, Object> map = new HashMap<>();
        for (int i = 1; i <= dataSize; i++) {
            Integer key = i;
            String value = RandomGenerator.generateRandomString(10);
            map.put(key, value);
            try {
                bPlusTree.insert(key, value);
            } catch (Exception e){
                e.printStackTrace();
                bPlusTree.out(true);
                throw new RuntimeException("treeInsert error: " + i);
            }

        }

        // 测试其他
        testBPlusTree(bPlusTree, map);

        System.out.printf("testSortedInt{degree=%d, dataSize=%d} success\n", degree, dataSize);
        hr(100);
    }

    /**
     * 测试 key 为 int 类型时的 B+ 树, key为逆序排序数据
     * @param degree B+ 树阶数
     * @param dataSize 插入数据量
     */
    public static void testReverseSortedInt(int degree, int dataSize){
        BPlusTree<Integer> bPlusTree = new BPlusTree<>(degree);

        // 插入数据测试
        Map<Integer, Object> map = new HashMap<>();
        for (int i = dataSize; i >= 1; i--) {
            Integer key = i;
            String value = RandomGenerator.generateRandomString(10);
            map.put(key, value);
            try {
                bPlusTree.insert(key, value);
            } catch (Exception e){
                e.printStackTrace();
                bPlusTree.out(true);
                throw new RuntimeException("treeInsert error: " + i);
            }

        }

        // 测试其他
        testBPlusTree(bPlusTree, map);

        System.out.printf("testReverseSortedInt{degree=%d, dataSize=%d} success\n", degree, dataSize);
        hr(100);
    }

    /**
     * 测试 key 为 String 类型时的 B+ 树, key为逆序排序数据
     * @param degree B+ 树阶数
     * @param dataSize 插入数据量
     */
    public static void testRandomStr(int degree, int dataSize){
        BPlusTree<String> bPlusTree = new BPlusTree<>(degree);

        // 插入数据测试
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= dataSize; i++) {
            String key = RandomGenerator.generateRandomString(10);
            String value = RandomGenerator.generateRandomString(10);
            map.put(key, value);
            try {
                bPlusTree.insert(key, value);
            } catch (Exception e){
                bPlusTree.out(true);
                e.printStackTrace();
                throw new RuntimeException("treeInsert error: " + i);
            }

        }

        // 测试其他
        testBPlusTree(bPlusTree, map);

        System.out.printf("testRandomStr{degree=%d, dataSize=%d} success\n", degree, dataSize);
        hr(100);
    }

    public static void main(String[] args) {
        // 测试小规模数据和不同阶数的数据
        for (int i = 10; i < 1000; i *= 2) {
            for (int j = 10; j < 10000; j *= 2) {
                try {
                    testRandomInt(i, j);
                    testSortedInt(i, j);
                    testReverseSortedInt(i, j);
                } catch (UniqueKeyException e){
                    e.printStackTrace();
                }
                testRandomStr(i, j);
            }
        }

        // 测试中规模数据
        for (int i = 0; i < 20; i++) {
            for (int j = 10000; j < 100000; j *= 2) {
                System.out.println(i);
                testRandomStr(1000, j);
            }
        }

        // 测试大规模数据
        for (int i = 0; i < 3; i++) {
            for (int j = 100000; j < 2000000; j *= 4) {
                System.out.println(i);
                testRandomStr(1000, j);
            }
        }

    }
}
