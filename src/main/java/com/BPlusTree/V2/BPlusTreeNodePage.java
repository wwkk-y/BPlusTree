package com.BPlusTree.V2;

import com.BPlusTree.BPLinkList.BPLinkListNode;
import com.BPlusTree.SortedLinkList.DisorderedException;
import com.BPlusTree.SortedLinkList.SortedLinkList;
import com.BPlusTree.SortedLinkList.SortedLinkListNode;
import com.BPlusTree.util.CompareUtil;
import lombok.NonNull;

/**
 * B+ 树节点页
 */
public class BPlusTreeNodePage <K extends Comparable<K>, V> {
    private final BPlusTree<K, V> bPlusTree; // 所属 B+ 树
    private boolean leaf; // 是否是叶子节点
    SortedLinkList<BPlusTreeNode<K, V>> nodes; // 节点页
    private BPlusTreeNodePage<K, V> parentPage; // 父页面
    private SortedLinkListNode<BPlusTreeNode<K, V>> parentListNode; // 父节点所在链表节点
    private BPLinkListNode<BPlusTreeNodePage<K, V>> leafListNode; // 所在叶子链表里的节点(当且仅当为叶子界面时才有)

    /**
     * 构造一个根节点
     */
    public BPlusTreeNodePage(@NonNull BPlusTree<K, V> bPlusTree){
        this.bPlusTree = bPlusTree;
        this.leaf = true;
        this.nodes = new SortedLinkList<>(bPlusTree.unique);
        this.leafListNode = bPlusTree.leafPageList.pushBack(this);
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
            if(CompareUtil.equal(leTreeNode.key, key)){
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
                    System.out.println(bPlusTree.toKVPairList());
                    System.out.println(key);
                    throw new RuntimeException(e);
                }
                trySplit();
            } else {
                BPlusTreeNode<K, V> lastKeyNode = nodes.lastElement();
                lastKeyNode.key = key;
                lastKeyNode.children.treeInsert(key, value);
            }
        } else {
            if(leaf){
                try {
                    nodes.insertBefore(leTreeNode, new BPlusTreeNode<>(key, true, value));
                } catch (DisorderedException e) {
                    System.out.println(bPlusTree.toKVPairList());
                    System.out.println(key);
                    System.out.println(leTreeNode.getData().key);
                    throw new RuntimeException(e);
                }
                trySplit();
            } else {
                leTreeNode.getData().children.treeInsert(key, value);
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

        if(parentListNode == null){
            // 为根界面
            // 分裂
            SortedLinkList<BPlusTreeNode<K, V>> rightList = nodes.midSplit();
            BPlusTreeNodePage<K, V> leftPage = new BPlusTreeNodePage<>(bPlusTree, leaf, nodes, this);
            BPlusTreeNodePage<K, V> rightPage = new BPlusTreeNodePage<>(bPlusTree, leaf, rightList, this);
            nodes = new SortedLinkList<>(bPlusTree.unique);

            // 设置索引
            // 子节点父节点相互绑定
            BPlusTreeNode<K, V> leftMaxNode = leftPage.nodes.lastElement();
            BPlusTreeNode<K, V> leftKeyNode = new BPlusTreeNode<>(leftMaxNode.key, leftPage);

            BPlusTreeNode<K, V> rightMaxNode = rightPage.nodes.lastElement();
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(rightMaxNode.key, rightPage);
            try {
                leftPage.parentListNode = nodes.pushBack(leftKeyNode);
                rightPage.parentListNode = nodes.pushBack(rightKeyNode);
            } catch (DisorderedException e) {
                throw new RuntimeException(e);
            }


            if(leaf){
                // 为叶子界面时需要更新 leafPageList
                leftPage.leafListNode = bPlusTree.leafPageList.insertAfter(leafListNode, leftPage);
                rightPage.leafListNode = bPlusTree.leafPageList.insertAfter(leftPage.leafListNode, rightPage);
                bPlusTree.leafPageList.removeNode(leafListNode);
                // 设置为非叶子节点
                leaf = false;
                leafListNode = null;
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
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(rightMaxNode.key, rightPage);

            // 父界面添加索引
            try {
                parentListNode.getData().key = leftMaxNode.key;
                rightPage.parentListNode = parentPage.nodes.insertAfter(parentListNode, rightKeyNode);
            } catch (DisorderedException e) {
                throw new RuntimeException(e);
            }

            // 为叶子界面时需要更新 leafPageList
            if(leaf){
                rightPage.leafListNode = bPlusTree.leafPageList.insertAfter(leafListNode, rightPage);
            } else {
                rightPage.nodes.forEach(node -> {
                    node.children.parentPage = rightPage;
                });
            }

            // 因为父页面节点增加, 尝试分裂
            parentPage.trySplit();
        }

        return true;
    }
}
