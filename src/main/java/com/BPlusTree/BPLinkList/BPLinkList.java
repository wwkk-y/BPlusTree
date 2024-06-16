package com.BPlusTree.BPLinkList;

import lombok.NonNull;

import java.util.function.Consumer;

public class BPLinkList <T>{
    private BPLinkListNode<T> head;
    private BPLinkListNode<T> tail;
    private long size;

    /**
     * 初始化为一个节点, 负责头尾指针和大小改变
     */
    private void init(@NonNull BPLinkListNode<T> newNode){
        head = newNode;
        tail = newNode;
        size = 1;
    }

    /**
     * 在 cur 后面插入新节点, 考虑头尾指针改变和改变大小
     */
    private void insertAfter(@NonNull BPLinkListNode<T> cur, @NonNull BPLinkListNode<T> newNode){
        /*
         cur <-> p2
         newNode
         转化成
         cur <-> newNode <-> p2
         */
        BPLinkListNode<T> p2 = cur.next;
        // cur -> next -> p2
        cur.next = newNode;
        newNode.next = p2;
        // cur <- newNode <- p2
        if(p2 != null){
            p2.pre = newNode;
        }
        newNode.pre = cur;

        // 可能改变了尾指针
        if(tail == cur){
            tail = newNode;
        }

        size += 1;
    }

    /**
     * 在 cur 前面插入新节点, 考虑头尾指针改变和改变大小
     */
    private void insertBefore(@NonNull BPLinkListNode<T> cur, @NonNull BPLinkListNode<T> newNode){
        /*
         p1 <-> cur
         newNode
         转化成
         p1 <-> newNode <-> cur
         */
        BPLinkListNode<T> p1 = cur.pre;
        // p1 -> newNode -> cur
        if(p1 != null){
            p1.next = newNode;
        }
        newNode.next = cur;
        // p1 <- newNode <- cur
        cur.pre = newNode;
        newNode.pre = p1;

        // 可能是头指针
        if(cur == head){
            head = newNode;
        }

        size += 1;
    }

    /**
     * 移除节点, 考虑头尾指针改变和改变大小
     */
    public void removeNode(@NonNull BPLinkListNode<T> node) {
        /*
            p1 <-> node <-> p2
            改成
            p1 <-> p2
         */
        BPLinkListNode<T> p1 = node.pre;;
        BPLinkListNode<T> p2 = node.next;
        if (p1 != null) {
            p1.next = p2;
        }
        if(p2 != null){
            p2.pre = p1;
        }

        // 考虑头尾指针改变
        if(head == node){
            head = p2;
        }
        if(tail == node){
            tail = p1;
        }

        size -= 1;
    }

    /**
     * 尾部添加新元素
     * @return 添加的节点
     */
    public BPLinkListNode<T> pushBack(T val){
        BPLinkListNode<T> newNode = new BPLinkListNode<>(val);
        if(tail == null){
            init(newNode);
        } else {
            insertAfter(tail, newNode);
        }

        return newNode;
    }

    /**
     * 清空链表
     */
    public void clear(){
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * 删除最后一个节点
     * @return 删除的节点
     */
    public BPLinkListNode<T> popBack(){
        BPLinkListNode<T> lastNode = tail;
        removeNode(tail);
        return lastNode;
    }

    /**
     * 在 node 后面插入新节点
     */
    public BPLinkListNode<T> insertAfter(@NonNull BPLinkListNode<T> node, T val){
        BPLinkListNode<T> newNode = new BPLinkListNode<>(val);
        insertAfter(node, newNode);
        return newNode;
    }


    /**
     * 遍历链表
     * @param action 执行的函数
     */
    public void forEach(@NonNull Consumer<? super T> action){
        BPLinkListNode<T> p = head;
        while (p != null){
            action.accept(p.data);
            p = p.next;
        }
    }

}
