package com.BPlusTree.SortedLinkList;

import lombok.Data;

/**
 * 有序的双向链表节点
 */
@Data
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
}
