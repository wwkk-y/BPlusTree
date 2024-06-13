package com.BPlusTree.SortedLinkList;

/**
 * 有序的双向链表节点
 */
public class SortedLinkListNode <T extends Comparable<T>>{
    SortedLinkListNode<T> pre;
    T data;
    SortedLinkListNode<T> next;

    SortedLinkListNode(T data){
        this.data = data;
    }

    SortedLinkListNode(SortedLinkListNode<T> pre, T data, SortedLinkListNode<T> next){
        this.pre = pre;
        this.data = data;
        this.next = next;
    }

    public T getData(){
        return data;
    }
}
