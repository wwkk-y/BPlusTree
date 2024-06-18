package com.BPlusTree.V3;

import com.BPlusTree.SortedLinkList.SortedLinkList;
import com.BPlusTree.util.CompareUtil;
import com.BPlusTree.util.TestUtil;

import java.util.*;
import java.util.stream.Collectors;

public class BPlusTree<K extends Comparable<K>, V> {

    final boolean unique; // 是否唯一, 唯一时不允许重复值
    final int degree; // 阶数
    SortedLinkList<BPlusTreeNode<K, V>> leafTreeNodeList; // 叶子节点链表
    BPlusTreeNodePage<K, V> rootPage; // 根界面

    public BPlusTree(boolean unique, int degree){
        this.unique = unique;
        this.degree = degree;
        this.leafTreeNodeList = new SortedLinkList<>(unique);
        this.rootPage = new BPlusTreeNodePage<>(this);
    }

    /**
     * 查找索引为 key 的数据
     * @return 没有数据时返回空数组
     */
    public ArrayList<V> select(K key){
        return rootPage.treeSelect(key);
    }

    /**
     * 插入数据
     * @param key 索引
     * @param val 值
     */
    public void insert(K key, V val){
        if(unique && key == null){
            throw new UniqueKeyNullException();
        }
        rootPage.treeInsert(key, val);
    }

    /**
     * 更新数据
     * @param key 索引
     * @param newVal 新值
     * @return 更新的行
     */
    public int update(K key, V newVal){
        return rootPage.treeUpdate(key, newVal);
    }

    /**
     * 测试方法, 广度优先遍历检查节点有没有问题
     * @param out 为 true 时输出
     * @param insertedKeys 插入的key
     * @param insertedValues 插入的values
     * @param dataHasNull 存入的 data 是否有 null
     */
    void check(boolean out,  ArrayList<K> insertedKeys, ArrayList<V> insertedValues, boolean dataHasNull){
        Queue<BPlusTreeNodePage<K, V>> queue = new LinkedList<>();
        queue.add(rootPage);

        ArrayList<K> keys = new ArrayList<>();
        ArrayList<V> values = new ArrayList<>();
        while (!queue.isEmpty()){
            BPlusTreeNodePage<K, V> cur = queue.poll();
            if(out){
                System.out.println(cur.nodes);
                TestUtil.hr();
            }

            // 检测链表 size
            if(cur.nodes.toList().size() != cur.nodes.getSize()){
                throw new RuntimeException("链表 size 错误");
            }

            // 检查个数
            if(cur.parentPage == null){
                // 不为根节点时检查个数
                if(cur.nodes.getSize() > degree){
                    throw new RuntimeException("节点页节点个数超出阶数: " + degree);
                }
            }

            // 检查索引
            if(cur.parentPage == null && cur.parentKeyNode != null){
                throw new RuntimeException("没有父页面时不应该有索引");
            }
            if(cur.parentPage != null && cur.parentKeyNode == null){
                throw new RuntimeException("有父界面时索引不存在");
            }
            if(cur.parentPage != null && CompareUtil.notEqual(cur.parentKeyNode.getData().key, cur.nodes.lastElement().key)){
                throw new RuntimeException("父界面索引为页里面索引的最大值");
            }

            cur.nodes.forEach(node -> {
                if(node.children != null){
                    queue.add(node.children);
                }

                // 检查节点
                if(node.leaf){
                    // 为叶子节点时
                    keys.add(node.key);
                    values.add(node.data);

                    if(node.children != null){
                        throw new RuntimeException("叶子节点不应该有孩子");
                    }
                    if(node.leafTreeNode == null){
                        throw new RuntimeException("叶子节点应该位于叶子节点链表中");
                    }
                    if(node.leafTreeNode.getData() != node){
                        throw new RuntimeException("叶子节点中绑定的节点不是自己");
                    }
                    if(!dataHasNull && node.data == null){
                        throw new RuntimeException("叶子节点应该有数据");
                    }
                } else {
                    // 不为叶子节点时
                    if(node.children == null){
                        throw new RuntimeException("非叶子节点应该有孩子");
                    }
                    if(node.leafTreeNode != null){
                        throw new RuntimeException("非叶子节点不应该位于叶子节点链表中");
                    }
                    if(node.data != null){
                        throw new RuntimeException("非叶子节点不应该有数据");
                    }
                }

                // 检查所属页
                if(node.page != cur){
                    System.out.println("insertedKeys: " + insertedKeys);
                    System.out.println("sorted: " + insertedKeys.stream().sorted().collect(Collectors.toList()));
                    System.out.println("keys: " + keys);
                    System.out.println("node: " + node);
                    System.out.println("data: " + node.data);
                    System.out.println("page: " + node.page);
                    System.out.println("root: " + rootPage);
                    throw new RuntimeException("所属页不对");
                }
                // 检查索引链表节点是否指向自己
                if(node.keyListNode == null || node.keyListNode.getData() != node){
                    System.out.println("node: " + node);
                    System.out.println("keyListNode: " + node.keyListNode);
                    throw new RuntimeException("索引链表节点没有指向自己");
                }
            });
        }

        if(out){
            System.out.printf("keys: %s\n", keys);
            TestUtil.hr();
        }
        // 检查 keys 是否排序
        if(!TestUtil.isSorted(keys)){
            throw new RuntimeException("未排序");
        }
        // 检查 keys 与 leafTreeNodeList 是否对应
        ArrayList<BPlusTreeNode<K, V>> leafNodeList = leafTreeNodeList.toList();
        List<K> leafKeyList = leafNodeList.stream().map(leafNode -> leafNode.key).collect(Collectors.toList());
        if(!Arrays.equals(keys.toArray(), leafKeyList.toArray())){
            throw new RuntimeException("keys 与叶子节点链表不对应");
        }

        if(insertedKeys != null){
            // 检查索引有没有漏或者错误数据
            if(!TestUtil.isArrayElementEqual(keys, insertedKeys)){
                System.out.println(keys);
                System.out.println(insertedKeys);
                throw new RuntimeException("插入的 key 不相等");
            }
        }

        if(out && insertedValues != null){
            System.out.printf("insertedValues: %s\n", insertedValues);
            TestUtil.hr();
        }

        if(out){
            TestUtil.hr();
        }
    }

    void check(boolean out,  ArrayList<K> insertedKeys, ArrayList<V> insertedValues){
        check(out, insertedKeys, insertedValues, false);
    }

    void check(boolean out){
        check(out, null, null, false);
    }
}
