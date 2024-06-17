package com.BPlusTree.V2;


import com.BPlusTree.BPLinkList.BPLinkList;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BPlusTree<K extends Comparable<K>, V> {

    /**
     * 键值对
     */
    @AllArgsConstructor
    @Data
    public static class KVPair<K extends Comparable<K>, V>{
        private K key;
        private V val;
    }

    final boolean unique; // 是否唯一, 唯一时不允许重复值
    final int degree; // 阶数
    BPLinkList<BPlusTreeNodePage<K, V>> leafPageList; // 叶子界面链表
    private BPlusTreeNodePage<K, V> rootPage; // 根界面

    public BPlusTree(boolean unique, int degree){
        this.unique = unique;
        this.degree = degree;
        this.leafPageList = new BPLinkList<>();
        this.rootPage = new BPlusTreeNodePage<>(this);
    }

    public V select(K key){
        return rootPage.treeSelect(key);
    }

    public void insert(K key, V val){
        rootPage.treeInsert(key, val);
    }

    /**
     * 更新
     * @return 更新行数
     */
    public int update(K key, V val){
        BPlusTreeNode<K, V> treeNode = rootPage.treeSelectNode(key);
        if(treeNode == null){
            return 0;
        }
        treeNode.data = val;
        return 1;
    }

    /**
     * 删除
     * @return 删除行数
     */
    public int delete(K key){
        return rootPage.treeDelete(key);
    }

    /**
     * 将B+树里保存的键值转化成键值对列表
     */
    public ArrayList<KVPair<K, V>> toKVPairList(){
        ArrayList<KVPair<K, V>> result = new ArrayList<>();
        leafPageList.forEach(leafPage -> {
            leafPage.nodes.forEach(treeNode -> {
                result.add(new KVPair<>(treeNode.key, treeNode.data));
            });
        });
        return result;
    }

    /**
     * debug 输出 遍历节点页看大小满不满足
     * @param out 为 true 时输出
     */
    void debug(boolean out){
        Queue<BPlusTreeNodePage<K, V>> queue = new LinkedList<>();
        queue.add(rootPage);

        while (!queue.isEmpty()){
            BPlusTreeNodePage<K, V> cur = queue.poll();
            if(out){
                System.out.println(cur.nodes);
            }
            if(cur.nodes.toList().size() != cur.nodes.getSize()){
                throw new RuntimeException("size错误");
            }
            cur.nodes.forEach(node -> {
                if(node.children != null){
                    queue.add(node.children);
                }
                if(node.leaf && node.children != null){
                    throw new RuntimeException("数据错误");
                }
                // 假设测试的时候没有放 data = null 的数据
                if(node.leaf && node.data == null){
                    throw new RuntimeException("数据错误");
                }
            });
        }
    }
}
