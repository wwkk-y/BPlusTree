package com.BPlusTree.V2;

import com.BPlusTree.SortedLinkList.SortedLinkList;
import com.BPlusTree.SortedLinkList.SortedLinkListNode;

/**
 * B+ 树节点页
 */
public class BPlusTreeNodePage <K extends Comparable<K>, V> {
    private SortedLinkList<BPlusTreeNode<K, V>> nodes; // 节点页
    private SortedLinkListNode<BPlusTreeNode<K, V>> parentListNode; // 父节点所在链表节点
    private int degree; // 阶数

    public BPlusTreeNodePage(int degree, boolean unique){;
        this.degree = degree;
        this.nodes = new SortedLinkList<>(unique);
    }

    /**
     * 查找 key 对应的值
     */
    public V treeSelect(K key){
        // 查找第一个 >= key 的节点
        BPlusTreeNode<K, V> leTreeNode = nodes.findFirstLeElement(new BPlusTreeNode<>(key));
        if(leTreeNode == null){
            return null;
        }

        // 是叶子节点, 就判断值是不是相等
        if(leTreeNode.leaf){
            if(leTreeNode.key.compareTo(key) == 0){
                return leTreeNode.data;
            } else {
                return null;
            }
        }

        // 不是叶子节点, 就继续搜索该节点的子节点页
        return leTreeNode.children.treeSelect(key);
    }

    /**
     * 插入数据 key: value
     */
    public void treeInsert(K key, V value){
        // 查找第一个 >= key 的节点

        // 是叶子节点, 尝试插入

            // 如果插入到了末尾, 通知父节点更改索引值

        //
    }

    /**
     * 尝试分裂当前界面, 只有当 节点数 > 阶数 时才分裂
     * @return 如果分裂了就返回 true
     */
    public boolean trySplit(){
        // 只有当 节点数 > 阶数 时才分裂
        if(nodes.getSize() > degree){
            return false;
        }

        if(parentListNode == null){
            // 为根界面
            nodes.midSplit();
        }

        return true;
    }
}
