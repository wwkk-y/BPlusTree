package com.BPlusTree.BPLinkList;

public class BPLinkListNode <T>{
    BPLinkListNode<T> pre;
    T data;
    BPLinkListNode<T> next;

    public BPLinkListNode(T data){
        this.data = data;
    }
}
