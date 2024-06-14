package com.BPlusTree.BPLinkList;

import lombok.NonNull;

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
    private void insertAfter(@NonNull BPLinkListNode<T> cur, BPLinkListNode<T> newNode){
        /*
         cur <-> p2
         newNode
         转化成
         cur <-> newNode <-> p2
         */
        BPLinkListNode<T> p2 = cur.next;
        // cur -> next -> p2
        cur.next = newNode;
        if(newNode != null){
            newNode.next = p2;
        }
        // cur <- newNode <- p2
        if(p2 != null){
            p2.pre = newNode;
        }
        if(newNode != null){
            newNode.pre = cur;
        }

        // 可能改变了尾指针
        if(tail == cur){
            tail = newNode;
        }

        size += 1;
    }

    /**
     * 在 cur 前面插入新节点, 考虑头尾指针改变和改变大小
     */
    private void insertBefore(@NonNull BPLinkListNode<T> cur, BPLinkListNode<T> newNode){
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
        if(newNode != null){
            newNode.next = cur;
        }
        // p1 <- newNode <- cur
        cur.pre = newNode;
        if(newNode != null){
            newNode.pre = p1;
        }

        // 可能是头指针
        if(cur == head){
            head = newNode;
        }

        size += 1;
    }

    /**
     * 移除节点, 考虑头尾指针改变和改变大小
     */
    private void removeNode(@NonNull BPLinkListNode<T> node) {
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


}
