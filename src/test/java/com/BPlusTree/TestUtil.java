package com.BPlusTree;

import com.BPlusTree.util.CompareUtil;

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
        list1.sort(CompareUtil::compare);
        list2.sort(CompareUtil::compare);
        return Arrays.equals(list1.toArray(), list2.toArray());
    }
}
