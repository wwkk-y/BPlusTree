package com.BPlusTree.V3;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.BPlusTree.util.RandomGenerator;
import com.BPlusTree.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

public class BPlusTreeTest {

    private static boolean debug = false;

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
    public static void testDelete(BPlusTree<String, String> bPlusTree, List<String> keys, List<String> values){
        // 测试删除不存在的元素
        for (int i = 0; i < 100; i++) {
            String key = RandomGenerator.generateRandomString(1);
            if(!keys.contains(key)){
                int result = bPlusTree.delete(key);
                if(result != 0){
                    throw new RuntimeException("删除出错");
                }
            }
        }
        if(keys.size() > 0){
            for (int i = 0; i < 100; i++) {
                String key = RandomGenerator.generateRandomString(keys.get(0).length());
                if(!keys.contains(key)){
                    int result = bPlusTree.delete(key);
                    if(result != 0){
                        throw new RuntimeException("删除出错");
                    }
                }
            }
        }
        for (int i = 0; i < 100; i++) {
            String key = RandomGenerator.generateRandomString(20);
            if(!keys.contains(key)){
                int result = bPlusTree.delete(key);
                if(result != 0){
                    throw new RuntimeException("删除出错");
                }
            }
        }

        // 测试删除存在的元素
        for (int i = 0; i < keys.size(); i++) {
            if(debug){
                bPlusTree.check(true);
                System.out.println("delete: " + keys.get(i));
            }
            // 删除后不应该有这个节点了
            int result = bPlusTree.delete(keys.get(i));
            ArrayList<String> select = bPlusTree.select(keys.get(i));
            if((result == 0 && bPlusTree.unique)|| select.size() > 0){
                System.out.println("\n\nkey: " + keys.get(i));
                System.out.println("select: " + select);
                if(!debug){
                    System.out.println("unique: " + bPlusTree.unique);
                    System.out.println("degree: " + bPlusTree.degree);
                    System.out.println("keys: " + keys);
                    System.out.println("values: " + values);
                }
                if(debug){
                    bPlusTree.check(true);
                    bPlusTree.delete(keys.get(i));
                }
                throw new RuntimeException("删除出错");
            }
        }
        bPlusTree.check(false);
        System.out.println("test delete success");
        TestUtil.hr();
    }

    /**
     * 测试查找
     */
    public static void testSelect(BPlusTree<String, String> bPlusTree, List<String> keys, List<String> values){
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
    public static void testUpdate(BPlusTree<String, String> bPlusTree, List<String> keys, List<String> values){
        for (int i = 0; i < keys.size(); i++) {
            values.set(i, RandomGenerator.generateRandomString(5));
            bPlusTree.update(keys.get(i), values.get(i));
            ArrayList<String> valueList = bPlusTree.select(keys.get(i));
            for (String value : valueList) {
                if(value != values.get(i)){
                    System.out.println("key: " + keys.get(i));
                    System.out.println("newValue: " + values.get(i));
                    System.out.println("select: " + valueList);
                    if(!debug){
                        System.out.println("unique: " + bPlusTree.unique);
                        System.out.println("degree: " + bPlusTree.degree);
                        System.out.println("keys: " + keys);
                        System.out.println("values: " + values);
                    }
                    throw new RuntimeException("更新错误");
                }
            }
        }
        System.out.println("test update success");
    }

    public static void testMain(String[] args) {
        // 测试一层
        for (int i = 0; i < 1000; i++) {
            int degree = RandomGenerator.generateRandomNumber(3, 200);
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

    /**
     * 复现场景
     * @param unique 是否唯一索引
     * @param degree 阶数
     * @param keysJson 索引JSON字符串
     * @param valuesJson 值JSON字符串
     */
    public static void reProduceScene(boolean unique, int degree, String keysJson, String valuesJson){
        debug = true;

        BPlusTree<String, String> bPlusTree = new BPlusTree<>(unique, degree);
        // keys
        JSONArray jsonArray = JSONUtil.parseArray(keysJson);
        List<String> keys = jsonArray.toList(String.class);
        // values
        jsonArray = JSONUtil.parseArray(valuesJson);
        List<String> values = jsonArray.toList(String.class);

        // 构建 b+ 树
        // 测试插入
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            String value = values.get(i);
            bPlusTree.insert(key, value);
        }
        bPlusTree.check(true, keys, values);
        System.out.println("test insert success");

        // 测试其他的
        testSelect(bPlusTree, keys, values);
        testUpdate(bPlusTree, keys, values);
        testDelete(bPlusTree, keys, values);

        TestUtil.hr();

        debug = false;
    }

    public static void main(String[] args) {
        reProduceScene(
                false,
                10,
                "[mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE, iPcgWjDldn, FhDdLjkq4i, eFOjfGO2vh, CsYefDKVOo, B2mD2j11NN, QWqTjMzp6a, Xqz7I4MWKt, mf7CUgBMgE]",
                "[lETOZ, 87XCi, sb1ni, PK9Ld, BuI0e, LOUzF, cvnUz, mai0F, b52cq, 9MYkH, 8pi2v, o46XQ, fwUIS, WvMSW, ff0le, bDQ9Z, uoIVT, DQuiX, AWPSg, 7mXrT, xSchf, Uou8Q, ZUQWw, dOs1M, vDsP8, wyrRu, jiD6B, kO0Dv, JWnAh, jTLKC, xfMUB, 7COfd, UxeSQ, gqe9c, AGPno, bRsGL, C5Yga, IaaVI, Rsx8V, PEHs3, iI2sf, 3oYZn, DzyKU, D0bCO, 4XdUA, WHyRN, 8TTBA, RLq32, 4n97v, og0de, mEsyI, rJV6Y, lq1da, G92Zh, Vp2iJ, dMRIc, VyM01, cWZkz, 71Wls, 2CLIP, Ejoc0, 8eqlB, 7RpHz, q49lT, eq4wL, St2Ka, VrvSa, hVt8X, yud4P, rWE91, 6wG46, y2DlM, qZq6c, IsqR2, SUA26, DADzL, kg7Av, ufK9b, OcmO6, WHgvc, jaDOE, FCSxs, zLQXm, QykGT, yvwbo, moL6W, 8Je8L, GpLTu, HP1Wf, 2bmvh, FiVkN, GcYAg, Hy4rl, z06hq, TYVDb, NYxMj, dhXS9, STfeQ, AZBTD, mtOIb, OEzuY, arKdw, hxpvj, IuWFu, FHPd0, VVkzW, ifzJ0, ENhe7, bwbrk, 8VYFe, kRv1U, q8YDj, HwuXD, SiYPx, T9944, Iwixg, KDWf4, UyQLW, PvT10, mklEN, G3ogm, F5ywB, 5xfKi, eNDJx, GqlPx, iFJPZ, kUT05, Clk5g, o9ut3, WU1Vm, im61P, hZova, y5Bsm, SrHr6, Q6ggN, DuGOU, smayM, lWSN7, 8YSCQ, 3Semy, yNXUH, id1My, qkgn6, 7j6GX, NzffH, OA7a6, 1dQQk, oHJDk, Fko6U, uHnjC, D7IpC, 4X0Fg, gwIQi, M736T, 7wro2, kooy5, oEDir, zyq77, Os6RH, AVEbl, NNau7, 99VOF, QakaK, 0B0IH, VZ5VI, TX8zW, VkkbO, wtSIn, oVUQE, McD3N, fbcbm, Vq1jm, M9sCk, gKrlG, ytcy6, Z9Wrx, fJXoe, njK3O, cxf6V, cTHWw, qSRBn, m7isO, zLBvP, uaVzC, hqk2x, Zd17V, z5jbW, VHS6W, t5Qkf, ZFxnl, ThGST, seFFu, nkQCH, fSvgl, j1are, cj1xm, LcqDH, Z8lCS, xY9O5, jupSV, syHah, fTlpd, 4onJi, l5vGS, a9GNd, KShyD, 1eXrl, CbmWT, lFh9j, g35zX, TzUdR, Wcnr7, xd90t, m5ybs, Riw2W, RpAci, NaQkq, VsaTn, Ea4ea, KGB3g, WPf7H, lPOCr, UyXpe, oexAg, vi9uI, 53726, DTAwj, En0oF, NYsJ0, d4F5x, xjt7q, K3t6E, nJbfm, 0ycpD, eEmCv, 3LJ4b, I01FX, tBYW9, qOvB8, TlyG8, uFJox, JVuZB, OUHLr, EEnIw, p1e7n, IpbVf, lDgdn, 8QqLP, itCTB, jOqOc, WHYrq, jFitb, T2zfp, cPNZz, M2GQP, IlqCO, pxb7j, RkZgE, GApbz, LritO, P3IeL, wjAqm, PFeFh, pq1mx, YfcdS, YgigY, OGUlZ, HpIvL, a4gbf, 4jzxu, yKhr8, wgqaa, OeoMt, WQ8ng, wQcHq, gId3g, LvK7F, NL8Kh, CK2qu, HCJE4, JhqgJ, mwwIs, zQvOs, IV6Ka, f2WY2, Rg2UZ, JufCs, RYwab, IWiky, oNAa2, a2mAD, MyxaX, mcsSI, t3ilx, NPKuQ, l4b0o, 311wO, p2VwX, rRQp9, oe7na, WdEra, VTKHQ, vTj1T, uPmWG, Wkjgn, 2gEnp, xlS7i, VryPS, ErOgX, Gi3Pm, FMXUS, uZ9ES, iEgWV, imOsw, zq62E, bUeTZ, cs0g2, gmNzB, EMw0j, v30yB, ejTNH, 8przl, 0Qz1d, PDmdJ, IOl1w, wuVGL, PfWJ2, UcqHi, SAiiy, NIdGf, CbjiA, ZCL9a, Yu440, NsUxc, ijvTg, VNYbw, xDaQk, Z2t1R, K8Ntt, rPZ69, 4QoDD, 1Txia, 94btd, iqbMn, jTQnl, 92YYp, GurHj, WRD4J, RNrOF, QZwmh, tz1ji, d70vG, ZCuD9, CB8Wa, efCph, 4jxP0, vy5D9, 77nHB, fanDd, 8P1CP, qznPr, scYHg, SzbD9, OS9EW, nsXdA, 5AOBN, jd8C8, FqpH4, KFuou, 0JEoh, b4j7g, 0Rj1B, 8rRJZ, DLods, vlTRy, yuUJk, NGYLw, v75wh, aJLbr, jdkDu, oI9kv, dNnrl, FR3xt, 19yDC, 2xwWs, Z2tZv, nz3Pz, bm0xa, 9bIrH, 72FRe, cNhRX, NGJbt, pVROl, logwc, Bu0oE, lD0YB, l8xyJ, PgDfg, UAule, wRJng, NHA0z, YZAcs, 3aZRT, mftmo, M2ri5, spK6Y, Rvg3b, gJ9x8, WKCfe, X2Nev, yefZq, QW1mW, a5ynE, E3UB1, ZZ3Dl, 3g4yC, sMAR6, zzjCX, rfuHn, xx0JZ, JXeBm, SPp53, iG0lq, EXPdq, LYtVZ, SAIIu, iEvGX, uGVBb, qUHfe, aRVzV, my0st, QEbsc, 7nJTp, lfJe0, yu64A, hI6g3, De1zd, lmkAR, fWCx2, SoZwW, wTcsX, Q1AoS, RIq2o, syki0, rrxrx, otySP, n4r5P, Bof3E, kUfMR, RvD1S, JxoVU, ojpx9, Zg7ge, GWfPY, mChl3, bC7SG, iv6tv, Ty3cx, 9PWJu, 416Pp, K4dBO, KCQYD, me2Bx, tCLT2, RDCHi, lkYRP, hy7gj, mCj2k, bhU90, aCG9m, QRyBR, 7BVz1, 3lrzm, RoCg0, UgRuc, ZNGft, YwpcL, OU6YH, mwZfU, 9BLF9, HWXdv, QyKwn, yPB5s, 2q2Fc, 0H1pO, 6ErVe, SDnYY, 8bOK6, DkIPT, 1wRJG, 4qGji, 9g1zz, 1ekd8, VIuBD, RbY8s, IF9xf, mxEX4, OuWDE, 1jgUK, MnD37, Q1EG4, OZyRI, CvVjl, tg6Ao, Qi2wr, wQb5N, DL1EQ, UDgcD, DQbRx, 36XID, yUYdA, s4nN4, cimC5, 0SEFk, a9npn, C5gYZ, 1lGXV, lnJeu, fzDE0, KgKzy, O6Xli, GTdjM, iywFP, 2JAHB, 2edQE, pbSpI, aJdcE, As2RG, 4f7VI, dTmUC, wmvQo, 3GZEC, OkrYO, XSvOr, yGx9X, htr5U, Poj6z, endKY, QkphG, r9MMR, Nstn7, LqYYs, gfC0x, 5AROQ, PAikG, NzEg6, roSEQ, gsEnU, rfGQu, Ru02J, NYt0y, bnLxI, SAILJ, MUfvy, SkPFe, Vopiw, dkpq9, wWhNa, Te01i, 4Ect2, vbKyS, 1zmfi, Qb9MD, 5GErR, LS1NR, 0HDT8, vr2Bd, EFk2r, bwb9U, fXDwz, yEi2P, A78lN, joKZt, 5tJOa, IJUwo, xqzEU, kQ69t, FGaoP, h70Mn, kYkFe, aMcwj, AUfY2, 7slNh, 6K2Qu, ZhzKv, gdIII, 3Do3N, d1det, lD2gI, w4Bgx, ZIyQU, jJqg6, cf4LF, W2fvY, KgIua, zgf48, lRK0y, qWX8q, Hl2v6, W7FSd, orKGG, i19eN, bzIpv, Getuk, YE3kx, McHKO, wImKe, Wbbn6, X7bfx, RzL92, NENjc, 2jJbV, GvhJr, DsdZY, TAhmu, 5QvaI, ZfdYe, MjeqT, jysHz, DQ2QM, R4wIs, bQndE, fRVeh, UdbjT, FMJ5y, hPOqH, DWP6u, 7ZAPG, wwGnZ, AgY4s, znr8m, 05s1M, CAbE6, MvPWB, bvouT, 9vVIW, X01pK, p5TiR, Y9RtD, NTzCk, SrwGn, w5p7k, haRcw, iStNs, GSLSA, 5aPEy, oV2nl, mV8gp, vYkzG, znLMC, Erx6m, hRiiZ, LloiT, cNL6T, aanWG, u8iXT, OSfOo, p4wXr, pUgbI, Rg33P, 1Bwe4, wXtEc, EW79i, WEGcv, V817O, 6MlAf, 4HzT8, Nt2Js, Zqk32, TsUxO, Ww9gU, QLqsp, VdXG1, LQv3i, 3axu0, QnPjF, UvFK5, qaBK1, J3b2q, VGVOA, s8s9M, dqmD8, XuPez, micUw, UYwFt, jceQY, w3jcV, 7rm03, UkzVK, GVGHX, 6fipm, KMp5E, YrsmR, luxKS, 8LzXA, T2fDJ, uOh8J, cFZeT, trXkm, jy3Ux, ECB0L, 5PXpi, 6bR7O, B5GY6, D10is, TIYIr, qw28h, 7HaIB, SZeQJ, 1rtWx, kDW9E, ujTFW, IQu0k, YIoHI, nATby, PW1xA, vNnU4, Nj7mZ, V79Dg, 1P1RD, TMOom, 8M5zw, 7yoxk, lk92n, yc7cA, 52lZ7, 4FCfb, 4w9lj, LOFAk]"
        );

        for (int i = 0; i < 20; i++) {
            testMain(args);
        }
    }
}
