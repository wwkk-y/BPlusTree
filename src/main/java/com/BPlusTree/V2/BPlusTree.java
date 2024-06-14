package com.BPlusTree.V2;


import com.BPlusTree.BPLinkList.BPLinkList;

public class BPlusTree<K extends Comparable<K>, V> {
    private BPlusTreeNodePage<K, V> rootPage; // 根界面
    private boolean unique; // 是否唯一, 唯一时不允许重复值
    private BPLinkList<BPlusTreeNodePage<K, V>> leafList; // 叶子节点链表

}
