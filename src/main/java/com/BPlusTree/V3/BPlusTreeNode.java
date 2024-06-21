package com.BPlusTree.V3;

import com.BPlusTree.SortedLinkList.SortedLinkListNode;
import com.BPlusTree.util.CompareUtil;

/**
 * B+ 树节点
 */
public class BPlusTreeNode<K extends Comparable<K>, V> implements Comparable<BPlusTreeNode<K, V>> {
    BPlusTree<K, V> bPlusTree; // 所在 B+ 树
    K key; // 索引值
    boolean leaf; // 是否为叶子
    BPlusTreeNodePage<K, V> children; // 子节点页
    V data; // 数据节点, 为叶子时才有
    SortedLinkListNode<BPlusTreeNode<K, V>> leafTreeNode; // 所在叶子节点链表 Node, 为叶子节点时才有
    BPlusTreeNodePage<K, V> page; // 当前节点所属页
    SortedLinkListNode<BPlusTreeNode<K, V>> keyListNode; // 索引链表节点

    BPlusTreeNode(BPlusTree<K, V> bPlusTree, K key){
        this.bPlusTree = bPlusTree;
        this.key = key;
    }

    BPlusTreeNode(BPlusTree<K, V> bPlusTree, K key, boolean leaf){
        this.bPlusTree = bPlusTree;
        this.key = key;
        this.leaf = leaf;
    }

    /**
     * 构建一个叶子节点
     */
    BPlusTreeNode(BPlusTree<K, V> bPlusTree, K key, V data, BPlusTreeNodePage<K, V> page){
        this.bPlusTree = bPlusTree;
        this.key = key;
        this.leaf = true;
        this.data = data;
        this.page = page;
    }

    /**
     * 创建一个非叶子节点
     */
    BPlusTreeNode(BPlusTree<K, V> bPlusTree, K key, BPlusTreeNodePage<K, V> children, BPlusTreeNodePage<K, V> page){
        this.bPlusTree = bPlusTree;
        this.key = key;
        this.children = children;
        this.page = page;
    }

    @Override
    public int compareTo(BPlusTreeNode<K, V> o) {
        return CompareUtil.compare(this.key, o.key);
    }

    @Override
    public String toString() {
        if(!leaf){
            return key.toString();
        } else {
            return String.format(" (%s: %s) ", key.toString(), data.toString());
        }
    }
}
