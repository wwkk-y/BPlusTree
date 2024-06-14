package com.BPlusTree.util;


/**
 * 比较器, 考虑各种情况
 */
public class CompareUtil {
    public static <T extends Comparable<T>> int compare(T a, T b) {
        // 处理两者都为 null 的情况
        if (a == null && b == null) {
            return 0;
        }

        // 处理 a 为 null 的情况
        if (a == null) {
            return -1;  // 约定 null 小于任意实际值
        }

        // 处理 b 为 null 的情况
        if (b == null) {
            return 1;  // 约定任意实际值大于 null
        }

        // 处理两个非 null 值的情况
        return a.compareTo(b);
    }

    public static <T extends Comparable<T>> boolean large(T a, T b) {
        return compare(a, b) > 0;
    }

    public static <T extends Comparable<T>> boolean small(T a, T b) {
        return compare(a, b) < 0;
    }

    public static <T extends Comparable<T>> boolean equal(T a, T b) {
        return compare(a, b) == 0;
    }

    public static <T extends Comparable<T>> boolean largeEqual(T a, T b) {
        return compare(a, b) >= 0;
    }

    public static <T extends Comparable<T>> boolean smallEqual(T a, T b) {
        return compare(a, b) <= 0;
    }
}
