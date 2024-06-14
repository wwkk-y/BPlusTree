package com.BPlusTree.V2;

import com.BPlusTree.SortedLinkList.DisorderedException;
import com.BPlusTree.SortedLinkList.SortedLinkList;
import com.BPlusTree.SortedLinkList.SortedLinkListNode;

/**
 * B+ 树节点页
 */
public class BPlusTreeNodePage <K extends Comparable<K>, V> {
    private int degree; // 阶数
    private boolean leaf; // 是否是叶子节点
    private SortedLinkList<BPlusTreeNode<K, V>> nodes; // 节点页
    private BPlusTreeNodePage<K, V> parentPage; // 父页面
    private SortedLinkListNode<BPlusTreeNode<K, V>> parentListNode; // 父节点所在链表节点

    public BPlusTreeNodePage(int degree, boolean leaf, boolean unique){
        this.degree = degree;
        this.leaf = leaf;
        this.nodes = new SortedLinkList<>(unique);
    }

    private BPlusTreeNodePage(int degree, boolean leaf, SortedLinkList<BPlusTreeNode<K, V>> nodes, BPlusTreeNodePage<K, V> parentPage){
        this.degree = degree;
        this.leaf = leaf;
        this.nodes = nodes;
        this.parentPage = parentPage;
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
        SortedLinkListNode<BPlusTreeNode<K, V>> leTreeNode = nodes.findFirstLeNode(new BPlusTreeNode<>(key));
        if(leTreeNode == null){
            if(leaf){
                try {
                    nodes.pushBack(new BPlusTreeNode<>(key, true, value));
                } catch (DisorderedException e) {
                    e.printStackTrace();
                }
            } else {
                BPlusTreeNode<K, V> lastKeyNode = nodes.lastElement();
                lastKeyNode.key = key;
                lastKeyNode.children.treeInsert(key, value);
            }
        }
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
            // 分裂
            SortedLinkList<BPlusTreeNode<K, V>> rightList = nodes.midSplit();
            BPlusTreeNodePage<K, V> leftPage = new BPlusTreeNodePage<>(degree, leaf, nodes, this);
            BPlusTreeNodePage<K, V> rightPage = new BPlusTreeNodePage<>(degree, leaf, rightList, this);

            // 设置索引
            // 子节点父节点相互绑定
            nodes.clear();
            BPlusTreeNode<K, V> leftMaxNode = leftPage.nodes.lastElement();
            BPlusTreeNode<K, V> leftKeyNode = new BPlusTreeNode<>(leftMaxNode.key, leftPage);

            BPlusTreeNode<K, V> rightMaxNode = rightPage.nodes.lastElement();
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(rightMaxNode.key, rightPage);
            try {
                leftPage.parentListNode = nodes.pushBack(leftKeyNode);
                rightPage.parentListNode = nodes.pushBack(rightKeyNode);
            } catch (DisorderedException e) {
                e.printStackTrace();
            }

            // 设置为非叶子节点
            leaf = false;
        } else {
            SortedLinkList<BPlusTreeNode<K, V>> rightList = nodes.midSplit();
            BPlusTreeNodePage<K, V> rightPage = new BPlusTreeNodePage<>(degree, leaf, rightList, parentPage);
            BPlusTreeNode<K, V> rightMaxNode = rightPage.nodes.lastElement();
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(rightMaxNode.key, rightPage);

            rightPage.parentListNode = parentPage.nodes.insertAfter(parentListNode, rightKeyNode);
            parentPage.trySplit();
        }

        return true;
    }
}
