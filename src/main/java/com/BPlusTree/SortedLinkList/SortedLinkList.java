package com.BPlusTree.SortedLinkList;

import com.BPlusTree.util.CompareUtil;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * 有序双向链表
 */
public class SortedLinkList<T extends Comparable<T>> {
    @Getter
    private SortedLinkListNode<T> head;
    @Getter
    private SortedLinkListNode<T> tail;
    @Getter
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
     * 初始化链表为一个节点, 考虑头尾指针改变和改变大小
     */
    private void init(@NonNull SortedLinkListNode<T> newNode){
        head = newNode;
        tail = newNode;
        newNode.pre = null;
        newNode.next = null;
        size = 1;
    }


    /**
     * 在 cur 后面插入新节点, 考虑头尾指针改变和改变大小
     */
    private void insertAfter(@NonNull SortedLinkListNode<T> cur, @NonNull SortedLinkListNode<T> newNode){
        /*
         cur <-> p2
         newNode
         转化成
         cur <-> newNode <-> p2
         */
        SortedLinkListNode<T> p2 = cur.next;
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
    private void insertBefore(@NonNull SortedLinkListNode<T> cur, @NonNull SortedLinkListNode<T> newNode){
        /*
         p1 <-> cur
         newNode
         转化成
         p1 <-> newNode <-> cur
         */
        SortedLinkListNode<T> p1 = cur.pre;
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
     * 移除节点, 考虑头尾指针改变和改变大小, 不会改变 node 的 pre 和 next
     */
    public void removeNode(@NonNull SortedLinkListNode<T> node) {
        /*
            p1 <-> node <-> p2
            改成
            p1 <-> p2
         */
        SortedLinkListNode<T> p1 = node.pre;;
        SortedLinkListNode<T> p2 = node.next;
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
     * 在 node 后面插入新值, 保证有序
     * @param node 不能为 null, 且必须为本链表的节点
     * @return 插入的节点
     */
    public SortedLinkListNode<T> insertAfter(@NonNull SortedLinkListNode<T> node, T val) throws DisorderedException {
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
        insertAfter(node, newNode);

        return newNode;
    }

    /**
     * 在 node 后面插入一个新节点，保证有序
     * @param node 不能为 null, 且必须为本链表的节点
     * @return 插入的节点
     */
    public SortedLinkListNode<T> insertBefore(SortedLinkListNode<T> node, T val) throws DisorderedException {
        SortedLinkListNode<T> p1 = node.pre;
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
        insertBefore(node, newNode);

        return newNode;
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
            init(newNode);

            return newNode;
        }

        // 查找第一个 >= val 的节点 cur
        SortedLinkListNode<T> leNode = findFirstLeNode(val);

        // 判断 leNode 是否为 null -> 需要更新尾指针
        if(leNode == null){
            insertAfter(tail, newNode);
            return newNode;
        }

        // 根据需求判断是否需要唯一
        if(unique && CompareUtil.equal(leNode.data, val)){
            throw new RepeatValueException("不能插入重复的值: " + val);
        }

        // 在 leNode 前面插入值
        insertBefore(leNode, newNode);

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

        removeNode(node);
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

    /**
     * 转换成list
     */
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
     * @throws DisorderedException 不满足排序顺序时抛出
     */
    public SortedLinkListNode<T> pushBack(T val) throws DisorderedException {
        // tail -> NULL
        // tail <-> newNode -> NULL
        SortedLinkListNode<T> newNode = new SortedLinkListNode<>(val);
        return pushBack(newNode);
    }

    /**
     * 在尾部添加一个节点, 要求插入值保证原链表有序, 否则抛出异常
     * @return 返回插入元素所在节点
     * @throws DisorderedException 不满足排序顺序时抛出
     */
    public SortedLinkListNode<T> pushBack(@NonNull SortedLinkListNode<T> newNode) throws DisorderedException {
        if(tail == null){
            init(newNode);
        } else {
            // 判断大小 和 唯一
            if(CompareUtil.large(tail.data, newNode.data)){
                throw new DisorderedException("尾部插入的元素小于最大值: " + tail.data + " < " + newNode.data);
            }
            if(unique && CompareUtil.equal(tail.data, newNode.data)){
                throw new RepeatValueException("不能插入重复的元素");
            }
            insertAfter(tail, newNode);
        }
        return newNode;
    }

    /**
     * 在头部添加一个节点, 要求插入值保证原链表有序, 否则抛出异常
     * @return 返回插入元素所在节点
     * @throws DisorderedException 不满足排序顺序时抛出
     */
    public SortedLinkListNode<T> pushFront(SortedLinkListNode<T> newNode) throws DisorderedException{
        if(head == null){
            init(newNode);
        } else {
            // 判断大小 和 唯一
            if(CompareUtil.small(head.data, newNode.data)){
                throw new DisorderedException("头部插入的元素大于最小值: " + head.data + " < " + newNode.data);
            }
            if(unique && CompareUtil.equal(head.data, newNode.data)){
                throw new RepeatValueException("不能插入重复的元素");
            }
            insertBefore(head, newNode);
        }
        return newNode;
    }

    /**
     * 删除头部元素
     * @return 删除的元素节点
     */
    public SortedLinkListNode<T> popFront(){
        SortedLinkListNode<T> delNode = head;
        removeNode(head);
        return delNode;
    }

    /**
     * 删除尾部元素
     * @return 删除的元素节点
     */
    public SortedLinkListNode<T> popBack() {
        SortedLinkListNode<T> delNode = tail;
        removeNode(delNode);
        return delNode;
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

    /**
     * 遍历链表
     * @param action 执行的函数
     */
    public void forEach(@NonNull Consumer<? super T> action){
        SortedLinkListNode<T> p = head;
        while (p != null){
            action.accept(p.data);
            p = p.next;
        }
    }

    /**
     * 合并list, list合并到右边
     */
    public void merge(SortedLinkList<T> list) throws DisorderedException {
        if(list == this){
            throw new RuntimeException("不可以合并自己");
        }

        if(list.unique != unique){
            throw new RuntimeException("不同类型链表不可合并");
        }
        if(list.size == 0){
            return;
        }

        size += list.getSize();
        if(head == null){
            head = list.head;
        } else {
            // 检查有序性
            if(CompareUtil.large(tail.data, list.head.data)){
                throw new DisorderedException();
            }
            // 检查唯一约束
            if(unique && CompareUtil.equal(tail.data, list.head.data)){
                throw new RepeatValueException();
            }
            // ... tail -> NULL, NULL <- head ...
            // ... tail <-> head ...
            tail.next = list.head;
            list.head.pre = tail;
        }
        // 更新 tail
        tail = list.tail;
    }

    /**
     * 合并list, list合并到左边
     */
    public void mergeLeft(SortedLinkList<T> list) throws DisorderedException {
        if(list == this){
            throw new RuntimeException("不可以合并自己");
        }

        if(list.unique != unique){
            throw new RuntimeException("不同类型链表不可合并");
        }
        if(list.size == 0){
            return;
        }

        size += list.getSize();
        if(tail == null){
            head = list.head;
            tail = list.tail;
        } else {
            // 检查有序性
            if(CompareUtil.small(head.data, list.tail.data)){
                throw new DisorderedException();
            }
            // 检查唯一约束
            if(unique && CompareUtil.equal(head.data, list.tail.data)){
                throw new RepeatValueException();
            }
            // ... tail -> NULL, NULL <- head ...
            // ... tail <-> head ...
            list.tail.next = head;
            head.pre = list.tail;
        }
        // 更新 head
        head = list.head;
    }
}
