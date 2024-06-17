package com.BPlusTree.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestUtil {
    // 输出分割线
    public static void hr(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print('-');
        }
        System.out.println("");
    }

    // 输出分割线
    public static void hr() {
        hr(50);
    }

    // 判断数组是否排序
    public static <T extends Comparable<T>> boolean isSorted(List<T> list) {
        ArrayList<T> sortedList = new ArrayList<>(list);
        sortedList.sort(CompareUtil::compare);
        return list.equals(sortedList);
    }

    /**
     * 判断两个数组包含的元素是否相同
     */
    public static <T extends Comparable<T>> boolean isArrayElementEqual(List<T> list1, List<T> list2){
        ArrayList<T> sortedList1 = new ArrayList<>(list1);
        ArrayList<T> sortedList2 = new ArrayList<>(list2);
        sortedList1.sort(CompareUtil::compare);
        sortedList2.sort(CompareUtil::compare);
        return Arrays.equals(sortedList1.toArray(), sortedList2.toArray());
    }
}
