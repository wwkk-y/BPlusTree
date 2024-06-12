package com.BPlusTree.V1;

import java.util.*;

/**
 * B+树 唯一索引
 * @param <T>
 */
public class BPlusTree<T extends Comparable<T>> {
    int degree; // 度数/阶数
    BPlusTreeNodePage<T> rootPage; // 根页面

    public BPlusTree(int degree) {
        this.degree = degree;
        this.rootPage = new BPlusTreeNodePage<T>(this.degree, true);
    }

    public void insert(T key, Object value){
        rootPage.treeInsert(key, value);
    }

    public BPlusTreeNodePage<T> searchPage(T key){
        return rootPage.treeSearchPage(key);
    }

    public Object searchValue(T key){
        return rootPage.treeSearchValue(key);
    }

    public void delete(T key){

    }


    /**
     * 广度优先遍历, 输出 Tree
     * @return keys values
     */
    public HashMap<String, Object> out(boolean print) {
        Queue<BPlusTreeNodePage<T>> queue = new LinkedList<>();
        queue.add(rootPage);

        ArrayList<T> keys = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();

        while(!queue.isEmpty()){
            BPlusTreeNodePage<T> first = queue.poll();
            if(first.leaf){
                keys.addAll(first.getKeys());
                values.addAll(first.getData());
            }
            queue.addAll(first.getChildren());

            if(print){
                System.out.print(first);
                System.out.println("");

            }
        }

        HashMap<String , Object> result = new HashMap<>();
        result.put("keys", keys);
        result.put("values", values);
        return result;
    }
}
