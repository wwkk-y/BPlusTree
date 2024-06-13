package com.BPlusTree.V2;

public class BPlusTree<K extends Comparable<K>, V> {
    BPlusTreeNode<K, V> root;
    private boolean unique; // 是否唯一, 唯一时不允许重复值
}
