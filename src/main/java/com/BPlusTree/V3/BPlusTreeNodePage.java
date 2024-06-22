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
     * 在树结构里查找第一个 >=key 的索引链表节点
     * @param key 索引
     * @return 索引链表节点
     */
    public SortedLinkListNode<BPlusTreeNode<K, V>> treeFindFirstLENode(K key){
        // 查找第一个大于等 key 的索引
        SortedLinkListNode<BPlusTreeNode<K, V>> leNode = nodes.findFirstLeNode(new BPlusTreeNode<>(bPlusTree, key));
        if(leNode == null){
            // 没找到, 说明不存在
            return null;
        }

        if(!leaf){
            // 不为叶子节点, 继续搜索
            return leNode.getData().children.treeFindFirstLENode(key);
        }

        return leNode;
    }

    /**
     * 在树结构里查找第一个 =key 的链表节点
     * @param key 索引
     * @return 链表节点
     */
    public SortedLinkListNode<BPlusTreeNode<K, V>> treeFindFirstEqualNode(K key){
        // 查找第一个大于等 key 的索引
        SortedLinkListNode<BPlusTreeNode<K, V>> leNode = treeFindFirstLENode(key);
        if(leNode == null){
            // 没找到, 说明不存在
            return null;
        }

        // 相等时返回节点
        if(CompareUtil.equal(key, leNode.getData().key)){
            return leNode;
        }

        return null;
    }

    /**
     * 在树结构里查找索引为 key 的数据
     * @return 没有数据时返回空数组
     */
    public ArrayList<V> treeSelect(K key) {
        // 查找第一个等于 key 的索引
        SortedLinkListNode<BPlusTreeNode<K, V>> eNode = treeFindFirstEqualNode(key);
        if(eNode == null){
            return new ArrayList<>();
        }
        // 位于叶子节点链表里的节点位置
        SortedLinkListNode<BPlusTreeNode<K, V>> eLeafNode = eNode.getData().leafTreeNode;

        ArrayList<V> result = new ArrayList<>();
        if(bPlusTree.unique){
            // 为唯一索引时直接返回这一条数据
            result.add(eLeafNode.getData().data);
        } else {
            // 不为唯一索引时需要扫描多行数据
            SortedLinkListNode<BPlusTreeNode<K, V>> curLeafNode = eLeafNode;
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
            BPlusTreeNode<K, V> newTreeNode = new BPlusTreeNode<>(bPlusTree, key, val, this);
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
                    newTreeNode.keyListNode = nodes.pushBack(newTreeNode);
                } catch (DisorderedException e) {
                    bPlusTree.check(true);
                    System.out.printf("insert: %s\n", newTreeNode);
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    // 维护叶子链表 bPlusTree.leafTreeNodeList
                    newTreeNode.leafTreeNode = bPlusTree.leafTreeNodeList.insertBefore(
                            leTreeNode.getData().leafTreeNode,
                            newTreeNode
                    );

                    // 当前节点页插入数据
                    newTreeNode.keyListNode = nodes.insertBefore(leTreeNode, newTreeNode);
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
            BPlusTreeNode<K, V> leftKeyNode = new BPlusTreeNode<>(bPlusTree, leftMaxNode.key, leftPage, this);

            BPlusTreeNode<K, V> rightMaxNode = rightPage.nodes.lastElement();
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(bPlusTree, rightMaxNode.key, rightPage, this);
            try {
                leftPage.parentKeyNode = nodes.pushBack(leftKeyNode);
                rightPage.parentKeyNode = nodes.pushBack(rightKeyNode);
            } catch (DisorderedException e) {
                throw new RuntimeException(e);
            }
            // 节点添加索引指针
            leftKeyNode.keyListNode = leftPage.parentKeyNode;
            rightKeyNode.keyListNode = rightPage.parentKeyNode;

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
            // 需要更换节点所属界面
            leftPage.nodes.forEach(node -> {
                node.page = leftPage;
            });
            rightPage.nodes.forEach(node -> {
                node.page = rightPage;
            });
        } else {
            // 不为根界面直接把右边分裂出来
            SortedLinkList<BPlusTreeNode<K, V>> rightList = nodes.midSplit();

            // 新索引
            BPlusTreeNode<K, V> leftMaxNode = nodes.lastElement();

            BPlusTreeNodePage<K, V> rightPage = new BPlusTreeNodePage<>(bPlusTree, leaf, rightList, parentPage);
            BPlusTreeNode<K, V> rightMaxNode = rightPage.nodes.lastElement();
            BPlusTreeNode<K, V> rightKeyNode = new BPlusTreeNode<>(bPlusTree, rightMaxNode.key, rightPage, parentPage);

            // 父界面添加索引
            try {
                parentKeyNode.getData().key = leftMaxNode.key;
                rightPage.parentKeyNode = parentPage.nodes.insertAfter(parentKeyNode, rightKeyNode);
            } catch (DisorderedException e) {
                throw new RuntimeException(e);
            }
            // 节点添加索引指针
            rightKeyNode.keyListNode = rightPage.parentKeyNode;

            // 不为叶子界面时需要更新 children
            if(!leaf){
                rightPage.nodes.forEach(node -> {
                    node.children.parentPage = rightPage;
                });
            }
            // 需要更换节点所属界面
            rightPage.nodes.forEach(node -> {
                node.page = rightPage;
            });

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
        // 查找第一个等于 key 的索引
        SortedLinkListNode<BPlusTreeNode<K, V>> eNode = treeFindFirstEqualNode(key);
        if(eNode == null){
            return 0;
        }
        // 位于叶子节点链表里的节点位置
        SortedLinkListNode<BPlusTreeNode<K, V>> eLeafNode = eNode.getData().leafTreeNode;

        int result = 0;
        if(bPlusTree.unique){
            // 为唯一索引时直接返回这一条数据
            eLeafNode.getData().data = newVal;
            result += 1;
        } else {
            // 不为唯一索引时需要扫描多行数据
            SortedLinkListNode<BPlusTreeNode<K, V>> curLeafNode = eLeafNode;
            while(curLeafNode != null && CompareUtil.equal(curLeafNode.getData().key, key)){
                curLeafNode.getData().data = newVal;
                result += 1;

                curLeafNode = curLeafNode.getNext();
            }
        }

        return result;
    }

    /**
     * 删除树结构里索引为 key 的节点
     * @param key 索引
     * @return 删除行数
     */
    public int treeDelete(K key){
        // 查找第一个 >= key 的 node
        SortedLinkListNode<BPlusTreeNode<K, V>> leNode = nodes.findFirstLeNode(new BPlusTreeNode<>(bPlusTree, key));
        if(leNode == null){
            return 0;
        }

        if(leaf){
            int result = 0;
            while (true){
                if(CompareUtil.notEqual(leNode.getData().key, key)){
                    break;
                }
                // 删除当前满足要求的节点
                bPlusTree.leafTreeNodeList.removeNode(leNode.getData().leafTreeNode);
                nodes.removeNode(leNode);
                result += 1;

                // 可能移除了最后一个节点
                if(parentPage != null && leNode.getNext() == null ){
                    if(leNode.getPre() != null){
                        // 删除了最大值, 需要更新索引
                        updateParentKey(leNode.getPre().getData().key);
                    } else {
                        // 删除了一整页, 删除索引
                        removeParentKey();
                    }
                    // 继续删除下一页
                    SortedLinkListNode<BPlusTreeNode<K, V>> nextLeafNode = leNode.getData().leafTreeNode.getNext();
                    if(nextLeafNode != null){
                        result += nextLeafNode.getData().page.treeDelete(key);
                    }
                    break;
                }

                // 当且仅当当前页索引个数小于 degree/2 且不为根节点时, 尝试扩展索引个数
                boolean changed = tryExtendOrMerge();
                // 唯一索引只会删除一个节点
                if(bPlusTree.unique){
                    break;
                }

                // 如果改变了当前页, 需要更新 leNode
                if(changed){
                    leNode = nodes.findFirstLeNode(new BPlusTreeNode<>(bPlusTree, key));
                } else {
                    leNode = leNode.getNext();
                }
                if(leNode == null){
                    break;
                }

            }

            return result;
        } else {
            return leNode.getData().children.treeDelete(key);
        }
    }

    /**
     * 当且仅当当前页索引个数小于 degree/2 且不为根节点时, 尝试扩展索引个数,
     * 优先向兄弟页借, 如果兄弟页节点也不够, 就和兄弟页合并(优先右页)
     * @return 有影响时, 返回 true
     */
    private boolean tryExtendOrMerge() {
        if(nodes.getSize() >= bPlusTree.degree / 2){
            // 当且仅当当前页索引个数小于 degree/2 时才拓展
            return false;
        }
        if(parentPage == null){
            // 根节点不需要考虑
            return false;
        }

        // 先考虑右边, 右边没有再考虑左边
        if(parentKeyNode.getNext() != null){
            BPlusTreeNodePage<K, V> rightBro = parentKeyNode.getNext().getData().children;
            if(rightBro.nodes.getSize() > bPlusTree.degree / 2){
                // 借
                SortedLinkListNode<BPlusTreeNode<K, V>> rightNode = rightBro.nodes.popFront();
                try {
                    nodes.pushBack(rightNode);

                    rightNode.getData().page = this;
                    if(!rightNode.getData().leaf){
                        rightNode.getData().children.parentPage = this;
                    }

                } catch (DisorderedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 合并
                try {
                    nodes.merge(rightBro.nodes);
                } catch (DisorderedException e) {
                    throw new RuntimeException(e);
                }
                // 删除兄弟的索引
                parentPage.nodes.removeNode(rightBro.parentKeyNode);
                rightBro.nodes.forEach(node -> {
                    if(!node.leaf){
                        node.children.parentPage = this;
                    }
                    node.page = this;
                });
                // 父页变小了
                parentPage.tryExtendOrMerge();
            }
            // 最大值变了, 更新索引值
            updateParentKey(nodes.lastElement().key);
        } else if(parentKeyNode.getPre() != null){
            // 除了根节点, 其他节点肯定至少有一个兄弟
            // 根节点前面已经考虑了, 所以到这一步 brother 肯定不为 null, 就不用判断了
            // (错, 有可能删除整页时影响了树结构, 举个例子, 根节点一个索引)
            BPlusTreeNodePage<K, V> leftBro = parentKeyNode.getPre().getData().children;
            // 可能为空, 需要更新索引
            if(nodes.getSize() == 0){
                updateParentKey(leftBro.nodes.lastElement().key);
            }
            if(leftBro.nodes.getSize() > bPlusTree.degree / 2){
                // 借
                SortedLinkListNode<BPlusTreeNode<K, V>> leftNode = leftBro.nodes.popBack();
                try {
                    nodes.pushFront(leftNode);
                } catch (DisorderedException e) {
                    throw new RuntimeException(e);
                }

                leftNode.getData().page = this;
                if(!leftNode.getData().leaf){
                    leftNode.getData().children.parentPage = this;
                }
                // 更新左边节点的索引值
                leftBro.parentKeyNode.getData().key = leftBro.nodes.lastElement().key;
            } else {
                // 合并
                try {
                    nodes.mergeLeft(leftBro.nodes);
                } catch (DisorderedException e) {
                    throw new RuntimeException(e);
                }
                // 删除兄弟的索引
                parentPage.nodes.removeNode(leftBro.parentKeyNode);
                leftBro.nodes.forEach(node -> {
                    if(!node.leaf){
                        node.children.parentPage = this;
                    }
                    node.page = this;
                });
                // 父页变小了
                parentPage.tryExtendOrMerge();
            }
        } else {
            // 前后都没有兄弟
            // 如果自己也被删完了, 需要删除父页里索引
            if(nodes.getSize() == 0){
                removeParentKey(); // 删除索引

            }
        }

        return true;
    }

    /**
     * 删除索引值
     */
    private void removeParentKey() {
        if(parentPage != null){
            parentPage.nodes.removeNode(parentKeyNode);
            if(parentPage.nodes.getSize() == 0){
                // 父界面也被删完了
                parentPage.removeParentKey();
            } else if(parentKeyNode.getNext() == null){
                // 父界面删除的是最大值
                parentPage.updateParentKey();
            }
        }
    }



    /**
     * 更新索引值
     * @param newKey 新值
     */
    private void updateParentKey(K newKey){
        // 更新父界面里的索引值, 如果父界面当前索引为最大值, 继续更新父页的父页
        if(parentPage != null){
            parentKeyNode.getData().key = newKey;
            if(parentKeyNode.getNext() == null){
                parentPage.updateParentKey(newKey);
            }
        }
    }

    /**
     * 更新索引值
     */
    private void updateParentKey(){
        updateParentKey(nodes.lastElement().key);
    }

}
