package com.BPlusTree.SortedLinkList;

import com.BPlusTree.util.CompareUtil;

import java.util.ArrayList;

/**
 * 有序双向链表
 */
public class SortedLinkList<T extends Comparable<T>> {
    private SortedLinkListNode<T> head;
    private SortedLinkListNode<T> tail;
    private long size;
    private final boolean unique; // 元素是否唯一, 唯一的时候不能出现重复值

    public SortedLinkList(boolean unique){
        this.unique = unique;
    }

    private SortedLinkList(SortedLinkListNode<T> head, SortedLinkListNode<T> tail, long size, boolean unique){
        this.head = head;
        this.tail = tail;
        this.size = size;
        this.unique = unique;
    }

    /**
     * 在 cur 后面插入新节点
     */
    private void insertAfter(SortedLinkListNode<T> cur, SortedLinkListNode<T> newNode){
        /*
         p1 <-> cur <-> p2
         newNode
         转化成
         p1 <-> cur <-> newNode <-> p2
         */
        SortedLinkListNode<T> p1 = null;
        SortedLinkListNode<T> p2 = null;
        if(cur != null){
            p1 = cur.pre;
            p2 = cur.next;
        }
        // p1 -> cur -> next -> p2
        if(cur != null){
            cur.next = newNode;
        }
        if(newNode != null){
            newNode.next = p2;
        }
        // p1 <- cur <- newNode <- p2
        if(p2 != null){
            p2.pre = newNode;
        }
        if(newNode != null){
            newNode.pre = cur;
        }
    }

    /**
     * 在 cur 前面插入新节点
     */
    private void insertBefore(SortedLinkListNode<T> cur, SortedLinkListNode<T> newNode){
        /*
         p1 <-> cur <-> p2
         newNode
         转化成
         p1 <-> newNode <-> cur <-> p2
         */
        SortedLinkListNode<T> p1 = null;
        SortedLinkListNode<T> p2 = null;
        if(cur != null){
            p1 = cur.pre;
            p2 = cur.next;
        }
        // p1 -> newNode -> cur -> p2
        if(p1 != null){
            p1.next = newNode;
        }
        if(newNode != null){
            newNode.next = cur;
        }
        // p1 <- newNode <- cur <- p2
        if(cur != null){
            cur.pre = newNode;
        }
        if(newNode != null){
            newNode.pre = p1;
        }
    }

    /**
     * 在 node 后面插入新值, 保证有序
     * @param node 不能为 null, 且必须为本链表的节点
     * @return 插入的节点
     */
    public SortedLinkListNode<T> insertAfter(SortedLinkListNode<T> node, T val) throws DisorderedException {
        // node <-> p2
        // node <-> newNode <-> p2
        SortedLinkListNode<T> p2 = node.next;
        // 保证有序
        if(CompareUtil.large(node.data, val)){
            throw new DisorderedException();
        }
        if(p2 != null && CompareUtil.small(p2.data, val)){
            throw new DisorderedException();
        }
        // 有 unique 时 唯一性约束
        if(unique && CompareUtil.equal(node.data, val)){
            throw new RepeatValueException();
        }
        if(p2 != null && unique && CompareUtil.equal(p2.data, val)){
            throw new RepeatValueException();
        }
        // 开始插入
        SortedLinkListNode<T> newNode = new SortedLinkListNode<>(val);
        // node -> newNode -> p2
        node.next = newNode;
        newNode.next = p2;
        // node <- newNode <- p2
        if(p2 != null){
            p2.pre = newNode;
        }
        newNode.pre = node;
        // 可能是在尾节点后插入数据
        if(node == tail){
            tail = newNode;
        }

        size += 1;
        return newNode;
    }

    /**
     * 在 node 后面插入一个新节点，保证有序
     * @param node 不能为 null, 且必须为本链表的节点
     * @return 插入的节点
     */
    public SortedLinkListNode<T> insertBefore(SortedLinkListNode<T> node, T val) throws DisorderedException {
        // p1 <-> node
        // p1 <-> newNode <-> node
        SortedLinkListNode<T> p1 = node.next;
        // 保证有序
        if(p1 != null && CompareUtil.large(p1.data, val)){
            throw new DisorderedException();
        }
        if(CompareUtil.small(node.data, val)){
            throw new DisorderedException();
        }
        // 有 unique 时 唯一性约束
        if(unique && CompareUtil.equal(node.data, val)){
            throw new RepeatValueException();
        }
        if(unique && p1 != null && CompareUtil.equal(p1.data, val)){
            throw new RepeatValueException();
        }
        // 开始插入
        SortedLinkListNode<T> newNode = new SortedLinkListNode<>(val);
        // p1 -> newNode -> node
        if(p1 != null){
            p1.next = newNode;
        }
        newNode.next = node;
        // p1 <- newNode <- node
        node.pre = newNode;
        newNode.pre = p1;
        // 可能是在头节点后插入数据
        if(node == head){
            head = newNode;
        }

        size += 1;
        return newNode;
    }


    /**
     * 移除节点
     */
    private void removeNode(SortedLinkListNode<T> node) {
        /*
            p1 <-> node <-> p2
            改成
            p1 <-> p2
         */
        SortedLinkListNode<T> p1 = null;
        SortedLinkListNode<T> p2 = null;
        if (node != null) {
            p1 = node.pre;
            p2 = node.next;
        }
        if (p1 != null) {
            p1.next = p2;
        }
        if(p2 != null){
            p2.pre = p1;
        }
    }

    /**
     * 查找第一个 >= val 的 Node
     */
    public SortedLinkListNode<T> findFirstLeNode(T val){
        SortedLinkListNode<T> p = head;
        while(p != null){
            if(CompareUtil.largeEqual(p.data, val)){
                return p;
            }
            p = p.next;
        }

        return null;
    }

    /**
     * 查找第一个 >= val 的 值
     */
    public T findFirstLeElement(T val){
        SortedLinkListNode<T> node = findFirstLeNode(val);
        if(node == null){
            return null;
        }
        return node.data;
    }

    /**
     * 插入数据, 如果不允许重复值时插入了重复值抛出 RepeatValueException
     */
    public SortedLinkListNode<T> insert(T val){
        SortedLinkListNode<T> newNode = new SortedLinkListNode<>(val);

        // 如果 head 为空 -> 直接添加数据
        if(head == null){
            head = newNode;
            tail = head;

            size += 1;
            return newNode;
        }

        // 查找第一个 >= val 的节点 cur
        SortedLinkListNode<T> leNode = findFirstLeNode(val);

        // 判断 leNode 是否为 null -> 需要更新尾指针
        if(leNode == null){
            insertAfter(tail, newNode);
            tail = newNode;

            size += 1;
            return newNode;
        }

        // 根据需求判断是否需要唯一
        if(unique && CompareUtil.equal(leNode.data, val)){
            throw new RepeatValueException("不能插入重复的值: " + val);
        }

        // 判断 leNode 是否为 head -> 需要在前面插入数据
        if(leNode == head){
            insertBefore(head, newNode);
            head = newNode;

            size += 1;
            return newNode;
        }

        // 在 leNode 前面插入值
        insertBefore(leNode, newNode);

        size += 1;
        return newNode;
    }

    /**
     * 删除数据
     * @param val 要删除的数据
     * @return 有删除就返回true
     */
    public boolean delete(T val){
        SortedLinkListNode<T> node = getNode(val);
        if(node == null){
            return false;
        }
        // 为 head
        if(node == head){
            head = head.next;
        }
        // 为 tail
        if(node == tail){
            tail = tail.pre;
        }
        removeNode(node);
        size -= 1;
        return true;
    }

    /**
     * 查找值为 val 的 Node
     */
    public SortedLinkListNode<T> getNode(T val){
        SortedLinkListNode<T> leNode = findFirstLeNode(val);
        if(leNode != null && CompareUtil.equal(leNode.data, val)){
            return leNode;
        }
        return null;
    }


    public long getSize() {
        return size;
    }

    public ArrayList<T> toList(){
        ArrayList<T> result = new ArrayList<>();

        SortedLinkListNode<T> p = head;
        if(p != null){
            result.add(p.data);
            p = p.next;
        }
        while(p != null){
            result.add(p.data);
            p = p.next;
        }

        return result;
    }

    @Override
    public String toString() {
        return "SortedLinkList{" +
                "data=" + toList() +
                ", size=" + size +
                '}';
    }

    /**
     * 均分链表
     * 如果大小为奇数, 左边为 n + 1, 右边为 n
     * 当前分裂成左边部分
     * @return 右边部分
     */
    public SortedLinkList<T> midSplit() {
        if(size < 2){
            return null;
        }

        // 快慢指针
        SortedLinkListNode<T> fast = head;
        SortedLinkListNode<T> slow = head;

        long slowSize = 1; // 默认有个 head, 大小为1

        while(true){
            if(fast.next == null){
                break;
            }
            fast = fast.next;
            if(fast.next == null){
                break;
            }
            fast = fast.next;
            slow = slow.next;
            slowSize += 1;
        }

        /*
            sp <-> slow <-> sn ... fp <-> fast <-> fn
            从 slow 右边断开
            left: sp <-> slow -> NULL
            right: NULL <- sn ... fp <-> fast <-> fn
            特殊情况:
            sp <-> slow <-> fast
         */
        SortedLinkListNode<T> sn = slow.next;
        slow.next = null;
        sn.pre = null;
        // left
        tail = slow;
        long rightSize = size - slowSize;
        size = slowSize;

        // right
        return new SortedLinkList<>(sn, fast, rightSize, unique);
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
     * 在尾部添加一个值, 要求插入值保证原链表有序, 否则抛出异常
     * @return 返回插入元素所在节点
     * @throws DisorderedException 新元素 < 最大值时抛出
     */
    public SortedLinkListNode<T> pushBack(T val) throws DisorderedException {
        // tail -> NULL
        // tail <-> newNode -> NULL
        SortedLinkListNode<T> newNode = new SortedLinkListNode<>(val);
        if(tail == null){
            tail = newNode;
            head = newNode;
        } else {
            // 判断大小 和 唯一
            if(CompareUtil.large(tail.data, val)){
                throw new DisorderedException("尾部插入的元素小于最大值: " + tail.data + " < " + val);
            }
            if(unique && CompareUtil.equal(tail.data, val)){
                throw new RepeatValueException("不能插入重复的元素");
            }
            insertAfter(tail, newNode);
            tail = newNode;
        }

        size += 1;
        return newNode;
    }

    /**
     * 获取最后一个元素
     */
    public T lastElement(){
        if(tail == null){
            return null;
        }
        return tail.data;
    }
}
