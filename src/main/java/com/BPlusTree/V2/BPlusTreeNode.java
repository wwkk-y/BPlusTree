package com.BPlusTree.V2;

import com.BPlusTree.SortedLinkList.SortedLinkList;

/**
 * B+ 树节点
 */
public class BPlusTreeNode<K extends Comparable<K>, V> implements Comparable<BPlusTreeNode<K, V>> {
    K key; // 索引值
    boolean leaf; // 是否为叶子
    BPlusTreeNodePage<K, V> children; // 子节点页
    V data; // 数据

    BPlusTreeNode(K key){
        this.key = key;
    }

    /**
     * 构建一份非叶子节点
     * @param key 索引
     * @param children 子页
     */
    BPlusTreeNode(K key, BPlusTreeNodePage<K, V> children){
        this.key = key;
        this.children = children;
    }

    BPlusTreeNode(K key, boolean leaf){
        this.key = key;
        this.leaf = leaf;
    }

    BPlusTreeNode(K key, boolean leaf, V data){
        this.key = key;
        this.leaf = leaf;
        this.data = data;
    }

    @Override
    public int compareTo(BPlusTreeNode<K, V> o) {
        return key.compareTo(o.key);
    }
}
