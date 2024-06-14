package com.BPlusTree.SortedLinkList;

import com.BPlusTree.util.RandomGenerator;

import java.util.ArrayList;

import static com.BPlusTree.TestUtil.*;

public class SortedLinkListTest {

    // 测试插入
    public static void testInsert(int size){
        // 整数
        SortedLinkList<Integer> intList = new SortedLinkList<>(true);
        for (int i = 0; i < size; i++) {
            int val = RandomGenerator.generateRandomNumber(1, Integer.MAX_VALUE);
            intList.insert(val);
        }
        ArrayList<Integer> intArrayList = intList.toList();
        // 验证大小对不对
        if(intArrayList.size() != intList.getSize() || intArrayList.size() != size){
            throw new RuntimeException("大小不对");
        }
        // 验证是否排好序
        if(!isSorted(intArrayList)){
            throw new RuntimeException("未排序");
        }

        // 字符串
        SortedLinkList<String> strList = new SortedLinkList<>(true);
        for (int i = 0; i < size; i++) {
            String val = RandomGenerator.generateRandomString(20);
            strList.insert(val);
        }
        ArrayList<String> strArrayList = strList.toList();
        // 验证大小对不对
        if(strArrayList.size() != strList.getSize() || strArrayList.size() != size){
            throw new RuntimeException("大小不对");
        }
        // 验证是否排好序
        if(!isSorted(strArrayList)){
            throw new RuntimeException("未排序");
        }

        // 允许重复
        SortedLinkList<Integer> intList2 = new SortedLinkList<>(false);
        for (int i = 0; i < size; i++) {
            int val = RandomGenerator.generateRandomNumber(1, 100);
            intList2.insert(val);
        }
        ArrayList<Integer> intArrayList2 = intList2.toList();
        // 验证大小对不对
        if(intArrayList2.size() != intList2.getSize() || intArrayList2.size() != size){
            throw new RuntimeException("大小不对");
        }
        // 验证是否排好序
        if(!isSorted(intArrayList2)){
            throw new RuntimeException("未排序");
        }

        System.out.println("success");
    }

    // 测试搜索
    public static void testSearch(int size){
        // 整数
        SortedLinkList<Integer> intList = new SortedLinkList<>(true);
        ArrayList<Integer> intData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int val = RandomGenerator.generateRandomNumber(1, Integer.MAX_VALUE);
            intData.add(val);
            intList.insert(val);
        }
        for (Integer val : intData) {
            if (val.compareTo(intList.getNode(val).data) != 0) {
                throw new RuntimeException("查找值不对");
            }
        }

        // 字符串
        SortedLinkList<String> strList = new SortedLinkList<>(true);
        ArrayList<String> strData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String val = RandomGenerator.generateRandomString(20);
            strData.add(val);
            strList.insert(val);
        }
        for (String val : strData) {
            if (val.compareTo(strList.getNode(val).data) != 0) {
                throw new RuntimeException("查找值不对");
            }
        }

        // 允许重复
        SortedLinkList<Integer> intList2 = new SortedLinkList<>(false);
        ArrayList<Integer> intData2 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int val = RandomGenerator.generateRandomNumber(1, 100);
            intData2.add(val);
            intList2.insert(val);
        }
        for (Integer val : intData2) {
            if (val.compareTo(intList2.getNode(val).data) != 0) {
                throw new RuntimeException("查找值不对");
            }
        }

        System.out.println("success");
    }

    // 测试删除
    public static void testDelete(int size){
        // 整数
        SortedLinkList<Integer> intList = new SortedLinkList<>(true);
        ArrayList<Integer> intData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int val = RandomGenerator.generateRandomNumber(1, Integer.MAX_VALUE);
            intData.add(val);
            intList.insert(val);
        }
        for (Integer val : intData) {
            if(!intList.delete(val)){
                throw new RuntimeException("删除错误");
            }
        }
        if(intList.getSize() != 0 || intList.toList().size() != 0){
            throw new RuntimeException("删除错误");
        }

        // 字符串
        SortedLinkList<String> strList = new SortedLinkList<>(true);
        ArrayList<String> strData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String val = RandomGenerator.generateRandomString(20);
            strData.add(val);
            strList.insert(val);
        }
        for (String val : strData) {
            if(!strList.delete(val)){
                throw new RuntimeException("删除错误");
            }
        }
        if(strList.getSize() != 0 || strList.toList().size() != 0){
            throw new RuntimeException("删除错误");
        }

        // 允许重复
        SortedLinkList<Integer> intList2 = new SortedLinkList<>(false);
        ArrayList<Integer> intData2 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int val = RandomGenerator.generateRandomNumber(1, 100);
            intData2.add(val);
            intList2.insert(val);
        }
        for (Integer val : intData2) {
            if(!intList2.delete(val)){
                throw new RuntimeException("删除错误");
            }
        }
        if(intList2.getSize() != 0 || intList2.toList().size() != 0){
            throw new RuntimeException("删除错误");
        }

        System.out.println("success");
    }

    public static SortedLinkList<String> generateStrList(int size){
        SortedLinkList<String> list = new SortedLinkList<String>(false);
        for (int i = 0; i < size; i++) {
            String val = RandomGenerator.generateRandomString(20);
            SortedLinkListNode<String> node = list.insert(val);
            if(node.getData().compareTo(val) != 0){
                throw new RuntimeException("插入数据不对");
            }
        }
        ArrayList<String> toList = list.toList();
        if(!isSorted(toList)){
            throw new RuntimeException("排序错误");
        }
        if(toList.size() != size || list.getSize() != size){
            throw new RuntimeException("大小错误: " + size + " - "+ toList.size());
        }
        return list;
    }

    public static SortedLinkList<Integer> generateIntList(int size){
        SortedLinkList<Integer> list = new SortedLinkList<>(false);
        for (int i = 0; i < size; i++) {
            Integer val = RandomGenerator.generateRandomNumber(1, Integer.MAX_VALUE);
            SortedLinkListNode<Integer> node = list.insert(val);
            if(node.getData().compareTo(val) != 0){
                throw new RuntimeException("插入数据不对");
            }
        }
        ArrayList<Integer> toList = list.toList();
        if(!isSorted(toList)){
            throw new RuntimeException("排序错误");
        }
        if(toList.size() != size || list.getSize() != size){
            throw new RuntimeException("大小错误");
        }
        return list;
    }

    public static<T extends Comparable<T>> void testList(SortedLinkList<T> list){
        ArrayList<T> toList = list.toList();
        if(!isSorted(toList)){
            throw new RuntimeException("排序错误");
        }
        if(toList.size() != list.getSize()){
            System.out.println(list);
            System.out.println(toList);
            throw new RuntimeException("大小错误: " + list.getSize() + " - "+ toList.size());
        }
    }

    // 测试均分list
    public static void testMidSplit(int size){
        SortedLinkList<Integer> list = generateIntList(size);
        SortedLinkList<Integer> right = list.midSplit();
        testList(list);
        testList(right);
        if(list.getSize() != (size + 1)/ 2){
            throw new RuntimeException("大小错误: " + list.getSize() + " - "+ (size + 1)/ 2);
        }
        if(right.getSize() != (size/ 2)){
            throw new RuntimeException("大小错误: " + list.getSize() + " - "+ (size)/ 2);
        }

        System.out.println("success");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            try {
                testInsert(3000);
                testSearch(3000);
                testDelete(3000);
            } catch (RepeatValueException e){
                e.printStackTrace();
            }
            hr();
        }
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            for (int j = 100; j < 10000; j *= 2) {
                testMidSplit(j);
                testMidSplit(j + 1);
            }
            hr();
        }

    }
}
