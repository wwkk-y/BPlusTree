package com.BPlusTree.V3;

import com.BPlusTree.SortedLinkList.DisorderedException;
import com.BPlusTree.SortedLinkList.SortedLinkList;
import com.BPlusTree.SortedLinkList.SortedLinkListNode;
import com.BPlusTree.util.CompareUtil;
import lombok.NonNull;

import java.util.ArrayList;

/**
 * B+ 树节点页
 */
public class BPlusTreeNodePage<K extends Comparable<K>, V> {
    final BPlusTree<K, V> bPlusTree; // 所属 B+ 树
    boolean leaf; // 是否是叶子节点
    SortedLinkList<BPlusTreeNode<K, V>> nodes; // 节点页
    BPlusTreeNodePage<K, V> parentPage; // 父页面
    SortedLinkListNode<BPlusTreeNode<K, V>> parentKeyNode; // 索引所在父页面链表节点

    /**
     * 创建一个根节点
     * @param bPlusTree 所属 b+ 树
     */
    BPlusTreeNodePage(@NonNull BPlusTree<K, V> bPlusTree){
        this.bPlusTree = bPlusTree;
        this.leaf = true;
        this.nodes = new SortedLinkList<>(bPlusTree.unique);
    }

    /**
     * 内部构造函数
     */
    private BPlusTreeNodePage(
            @NonNull BPlusTree<K, V> bPlusTree,
            boolean leaf,
            SortedLinkList<BPlusTreeNode<K, V>> nodes,
            BPlusTreeNodePage<K, V> parentPage
    ){
        this.bPlusTree = bPlusTree;
        this.leaf = leaf;
        this.nodes = nodes;
        this.parentPage = parentPage;
    }

    /**
     * 查找第一个 >=key 的节点, 返回叶子节点链表里该节点的位置
     * 查找第一个 >=key 的节点在叶子节点链表里该节点的位置
     * @param key 索引
     * @return 叶子节点链表里该节点的位置
     */
    public SortedLinkListNode<BPlusTreeNode<K, V>> findFirstLELeafNode(K key){
        // 查找第一个大于等 key 的索引
        BPlusTreeNode<K, V> leKeyNode = nodes.findFirstLeElement(new BPlusTreeNode<>(bPlusTree, key));
        if(leKeyNode == null){
            // 没找到, 说明不存在
            return null;
        }

        if(!leaf){
            // 不为叶子节点, 继续搜索
            return leKeyNode.children.findFirstLELeafNode(key);
        } else if(CompareUtil.equal(key, leKeyNode.key)){
            return leKeyNode.leafTreeNode;
        }

        return null;
    }

    /**
     * 在树结构里查找索引为 key 的数据
     * @return 没有数据时返回空数组
     */
    public ArrayList<V> treeSelect(K key) {
        // 查找第一个大于等 key 的索引 位于叶子节点链表里的节点位置
        SortedLinkListNode<BPlusTreeNode<K, V>> leLeafNode = findFirstLELeafNode(key);
        if(leLeafNode == null){
            return new ArrayList<>();
        }

        // key 不相等
        if(CompareUtil.notEqual(key, leLeafNode.getData().key)){
            return new ArrayList<>();
        }

        // 为叶子节点且找到了
        ArrayList<V> result = new ArrayList<>();
        if(bPlusTree.unique){
            // 为唯一索引时直接返回这一条数据
            result.add(leLeafNode.getData().data);
        } else {
            // 不为唯一索引时需要扫描多行数据
            SortedLinkListNode<BPlusTreeNode<K, V>> curLeafNode = leLeafNode;
            while(curLeafNode != null && CompareUtil.equal(curLeafNode.getData().key, key)){
                result.add(curLeafNode.getData().data);
                curLeafNode = curLeafNode.getNext();
            }
        }
        return result;
    }

    /**
     * 在树结构里插入键值对
     * @param key 索引
     * @param val 值
     */
    public void treeInsert(K key, V val) {
        // 寻找当前页大于等于 key 的最大索引
        SortedLinkListNode<BPlusTreeNode<K, V>> leTreeNode = nodes.findFirstLeNode(new BPlusTreeNode<>(bPlusTree, key));
        if(leaf){
            BPlusTreeNode<K, V> newTreeNode = new BPlusTreeNode<>(bPlusTree, key, val);
            if(leTreeNode == null){
                try {
                    // 维护叶子链表 bPlusTree.leafTreeNodeList
                    if(nodes.getSize() == 0){
                        // 当且仅当刚初始状态什么元素都没有时, 索引页 nodes 才可能为空。
                        newTreeNode.leafTreeNode = bPlusTree.leafTreeNodeList.pushBack(newTreeNode);
                    } else {
                        // 最大值, 在 nodes 最后一个元素后面插入数据
                        newTreeNode.leafTreeNode = bPlusTree.leafTreeNodeList.insertAfter(
                                nodes.lastElement().leafTreeNode,
                                newTreeNode
                        );
                    }

                    // 当前节点页插入数据
                    nodes.pushBack(newTreeNode);
                } catch (DisorderedException e) {
                    bPlusTree.check(true);
                    System.out.printf("insert: %s\n", newTreeNode);
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    BPlusTreeNode<K, V> newNode = new BPlusTreeNode<>(bPlusTree, key, val);
                    // 维护叶子链表 bPlusTree.leafTreeNodeList
                    newNode.leafTreeNode = bPlusTree.leafTreeNodeList.insertBefore(
                            leTreeNode.getData().leafTreeNode,
                            newNode
                    );

                    // 当前节点页插入数据
                    nodes.insertBefore(leTreeNode, newNode);
                } catch (DisorderedException e) {
                    bPlusTree.check(true);
                    System.out.printf("insert: %s\n", newTreeNode);
                    System.out.printf("leTreeNode: %s\n", leTreeNode.getData().toString());
                    throw new RuntimeException(e);
                }
            }
            trySplit();
        } else {
            if(leTreeNode == null){
                // key 比所有索引都大
                // 更新最大值索引
                BPlusTreeNode<K, V> lastKeyNode = nodes.lastElement();
                lastKeyNode.key = key;
                // 在最后一个子页面插入数据
                lastKeyNode.children.treeInsert(key, val);
            } else {
                leTreeNode.getData().children.treeInsert(key, val);
            }
        }
    }

    /**
     * 尝试分裂当前界面, 只有当 节点数 > 阶数 时才分裂
     * @return 如果分裂了就返回 true
     */
    public boolean trySplit(){
        // 只有当 节点数 > 阶数 时才分裂
        if(nodes.getSize() <= bPlusTree.degree){
            return false;
        }

        if(parentPage == null){
            // 为根界面
            // 分裂
            SortedLinkList<BPlusTreeNode<K, V>> rightList = nodes.midSplit();
            BPlusTreeNodePage<K, V> leftPage = new BPlusTreeNodePage<>(bPlusTree, leaf, nodes, this);
            BPlusTreeNodePage<K, V> rightPage = new BPlusTreeNodePage<>(bPlusTree, leaf, rightList, this);
            nodes = new SortedLinkList<>(bPlusTree.unique);

            // 设置索引
            // 子节点父节点相互绑定
            BPlusTreeNode<K, V> leftMaxNode = leftPage.nodes.lastElement();
            BPlusTreeNode<K, V> leftKeyNode = new BPlusTreeNode<>(bPlusTree, leftMaxNode.key, leftPage);

            BPlusTreeNode<K, V> rightMaxNode = rightPage.nodes.lastElement();
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(bPlusTree, rightMaxNode.key, rightPage);
            try {
                leftPage.parentKeyNode = nodes.pushBack(leftKeyNode);
                rightPage.parentKeyNode = nodes.pushBack(rightKeyNode);
            } catch (DisorderedException e) {
                throw new RuntimeException(e);
            }

            if(leaf){
                // 设置为非叶子节点
                leaf = false;
            } else {
                // 需要更换子页面的父页面
                leftPage.nodes.forEach(node -> {
                    node.children.parentPage = leftPage;
                });
                rightPage.nodes.forEach(node -> {
                    node.children.parentPage = rightPage;
                });
            }
        } else {
            // 不为根界面直接把右边分裂出来
            SortedLinkList<BPlusTreeNode<K, V>> rightList = nodes.midSplit();

            // 新索引
            BPlusTreeNode<K, V> leftMaxNode = nodes.lastElement();

            BPlusTreeNodePage<K, V> rightPage = new BPlusTreeNodePage<>(bPlusTree, leaf, rightList, parentPage);
            BPlusTreeNode<K, V> rightMaxNode = rightPage.nodes.lastElement();
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(bPlusTree, rightMaxNode.key, rightPage);

            // 父界面添加索引
            try {
                parentKeyNode.getData().key = leftMaxNode.key;
                rightPage.parentKeyNode = parentPage.nodes.insertAfter(parentKeyNode, rightKeyNode);
            } catch (DisorderedException e) {
                throw new RuntimeException(e);
            }

            // 不为叶子界面时需要更新 children
            if(!leaf){
                rightPage.nodes.forEach(node -> {
                    node.children.parentPage = rightPage;
                });
            }

            // 因为父页面节点增加, 尝试分裂
            parentPage.trySplit();
        }

        return true;
    }


    /**
     * 在树结构里更新 key 对应的所有值
     * @param key 索引
     * @param newVal 新值
     * @return 更新行数
     */
    public int treeUpdate(K key, V newVal) {
        // 查找第一个 >=key 的节点在叶子节点链表里该节点的位置
        SortedLinkListNode<BPlusTreeNode<K, V>> leLeafNode = findFirstLELeafNode(key);
        if(leLeafNode == null){
            return 0;
        }

        // key 不相等
        if(CompareUtil.notEqual(key, leLeafNode.getData().key)){
            return 0;
        }

        int result = 0;
        if(bPlusTree.unique){
            // 为唯一索引时直接返回这一条数据
            leLeafNode.getData().data = newVal;
            result += 1;
        } else {
            // 不为唯一索引时需要扫描多行数据
            SortedLinkListNode<BPlusTreeNode<K, V>> curLeafNode = leLeafNode;
            while(curLeafNode != null && CompareUtil.equal(curLeafNode.getData().key, key)){
                leLeafNode.getData().data = newVal;
                result += 1;

                curLeafNode = curLeafNode.getNext();
            }
        }

        return result;
    }
}
