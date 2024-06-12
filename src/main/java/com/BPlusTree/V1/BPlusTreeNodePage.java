package com.BPlusTree.V1;

import lombok.Data;
import lombok.ToString;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据页面为一组Node的集合
 *  T: 索引(键) 类型
 *  非叶子节点里存的值是子节点里的最大值
 *  这里实现唯一索引 即 key 唯一
 */
@Data
@ToString(exclude = {"parent", "children"})
public class BPlusTreeNodePage<T extends Comparable<T>> implements Serializable{
    int degree; // 阶数/度数
    List<T> keys; // 键数组

    boolean leaf; // 是否为叶子页面
    List<BPlusTreeNodePage<T>> children; // 子页面的数组
    List<Object> data; // 数据, 为叶子时才有

    BPlusTreeNodePage<T> parent; // 父界面
    int indexInParent; // 父界面节点所在位置


    public BPlusTreeNodePage(int degree, boolean leaf) {
        this.degree = degree;
        this.keys = new ArrayList<>();
        this.leaf = leaf;
        this.children = new ArrayList<>();
        this.data = new ArrayList<>();
        this.parent = null;
    }

    /**
     * 对象转成 byte[] 便于持久化的序列化方法
     */
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  反序列化, byte[] 构造对象
     *  serialize() 的反函数
     */
    public static <T extends Comparable<T>> BPlusTreeNodePage<T> deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (BPlusTreeNodePage<T>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找第一个 >= key 的元素的位置
     * @return 第一个 >= key 的元素的位置
     */
    private int findFirstLEIndex(T key) {
        int low = 0;
        int high = keys.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            T midKey = keys.get(mid);

            int cmp = key.compareTo(midKey);
            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return low;
    }

    /**
     * B+ 树里查找数据页
     * @param key 索引
     */
    public BPlusTreeNodePage<T> treeSearchPage(T key) {
        // 找到第一个大于或等于key的位置
        int leI = findFirstLEIndex(key);

        if(leI >= keys.size()){
            // 表示当前页面最大的节点索引都没有 key 大, 即要找的元素不存在
            return null;
        }

        // 判断是不是叶子页
        if(leaf){
            if(key.compareTo(keys.get(leI)) == 0){
                return this;
            } else {
                return null;
            }
        }

        // 表示 key 在当前页面第 leI 个节点的范围里, 继续去子页面里搜索,
        return children.get(leI).treeSearchPage(key);
    }

    /**
     * B+ 树里查找数据
     * @param key 索引
     */
    public Object treeSearchValue(T key) {
        // 找到第一个大于或等于key的位置
        int leI = findFirstLEIndex(key);

        if(leI > keys.size()){
            // 表示当前页面最大的节点索引都没有 key 大, 即要找的元素不存在
            return null;
        }

        // 判断是不是叶子页
        if(leaf){
            if(key.compareTo(keys.get(leI)) == 0){
                return this.data.get(leI);
            } else {
                return null;
            }
        }

        // 表示 key 在当前页面第 leI 个节点的范围里, 继续去子页面里搜索,
        return children.get(leI).treeSearchValue(key);
    }

    /**
     * 分裂算法, 将当前界面对半分成两个界面
     * 当且仅当 keys.size() >= degree 时才分裂
     * @return 执行了分裂就返回 true
     */
    public boolean split(){
        if(keys.size() < degree){
            return false;
        }
        if(parent == null){
            // 当前节点为根节点, 对半分成根界面的两个子节点界面
            BPlusTreeNodePage<T> left = new BPlusTreeNodePage<>(degree, leaf);
            BPlusTreeNodePage<T> right = new BPlusTreeNodePage<>(degree, leaf);

            // 设置数据, [0, splitIndex) 给 left, [splitIndex, size) 给 right
            int splitIndex = (degree + 1) / 2;
            for (int i = 0; i < splitIndex; i++) {
                left.keys.add(keys.get(i));
                if(leaf){
                    left.data.add(data.get(i));
                } else{
                    left.children.add(children.get(i));
                    children.get(i).parent = left;
                    children.get(i).indexInParent = i;
                }
            }
            for(int i = splitIndex; i < keys.size(); ++i){
                right.keys.add(keys.get(i));
                if(leaf){
                    right.data.add(data.get(i));
                } else{
                    right.children.add(children.get(i));
                    children.get(i).parent = right;
                    children.get(i).indexInParent = i - splitIndex;
                }
            }


            // 绑定子节点
            keys.clear();
            keys.add(left.getKeys().get(left.keys.size() - 1));
            keys.add(right.getKeys().get(right.keys.size() - 1));

            children.clear();

            children.add(left);
            left.parent = this;
            left.indexInParent = 0;

            children.add(right);
            right.parent = this;
            right.indexInParent = 1;

            data.clear();


            // 根节点设置为非叶子节点
            leaf = false;
            data.clear();
        } else {
            // 假设当前节点为 nodePage
            // 1. nodePage 把右半边的数据新生成一个界面 right
            BPlusTreeNodePage<T> right = new BPlusTreeNodePage<>(degree, leaf);
            int splitIndex = (degree + 1) / 2;
            for(int i = splitIndex; i < keys.size(); ++i){
                right.keys.add(keys.get(i));
                if(leaf){
                    right.data.add(data.get(i));
                } else {
                    right.children.add(children.get(i));
                    children.get(i).parent = right;
                    children.get(i).indexInParent = i - splitIndex;
                }
            }


            // 2. 更新 nodePage 和其在父节点里的索引值
            keys.subList(splitIndex, keys.size()).clear();
            if(leaf){
                data.subList(splitIndex, data.size()).clear();
            } else {
                children.subList(splitIndex, children.size()).clear();
            }
            parent.keys.set(indexInParent, keys.get(keys.size() - 1));


            // 3. 在父节点 nodePage 索引值右边插入 right 索引值
            parent.keys.add(indexInParent + 1, right.keys.get(right.keys.size() - 1));

            // 4. 绑定父节点和 right, , 插入时注意更新后面被影响的节点记录的位置信息
            right.indexInParent = indexInParent + 1;
            right.parent = parent;
            parent.children.add(indexInParent + 1, right);
            for (int i = indexInParent + 2; i < parent.keys.size(); i++) {
                parent.children.get(i).indexInParent = i;
            }

            // 5. 判断父节点的索引值个数超了没有, 如果超了, 对父节点继续执行分裂算法
            if(parent.keys.size() > degree){
                parent.split();
            }
        }

        return true;
    }

    /**
     * B+ 树里插入数据
     * @param key 索引
     * @param value 值
     * @throws UniqueKeyException 插入重复的索引时
     */
    public void treeInsert(T key, Object value) {
        // 2. 在当前页面已有 keys 里找第一个 >= key 的位置 leI (large equal index)
        int leI = findFirstLEIndex(key);
        // 3. 判断 leI 是否超出当前页面已有索引 keys 的大小
        if(leI >= keys.size()){
            // 表示要插入的数据比所有数据都要大, 判断当前界面是不是叶子
            if(leaf){
                // 在末尾位置插入数据
                keys.add(key);
                data.add(value);

                // 更新父节点(如果有)里的索引值为当前值(保证是最大值), 循环往上, 直到没有父节点或者不是最后一个孩子
                BPlusTreeNodePage<T> cur = this;
                while(cur.parent != null && cur.indexInParent == cur.parent.keys.size() - 1){
                    cur.parent.keys.set(cur.indexInParent, key);
                    cur = cur.parent;
                }

                if(keys.size() > degree){
                    // 分裂当前界面, 执行分裂算法(具体思路在后面)
                    split();
                }
            } else {
                // 直接去最后一个子节点里尝试插入数据, 当前界面设为 nodePage.children[nodePage.children - 1], 执行第2步
                children.get(children.size() - 1).treeInsert(key, value);
            }
        } else {
            // 判断 keys[leI] == key
            if(key.compareTo(keys.get(leI)) == 0){
                // 表示键已经存在了, 抛出异常
                throw new UniqueKeyException("唯一索引不能插入重复的 key: " + key);
            } else {
                // 判断当前界面是不是叶子
                if(leaf){
                    // 在 leI 位置插入数据
                    keys.add(leI, key);
                    data.add(leI, value);
                    // 分裂当前界面, 执行分裂算法(具体思路在后面)
                    if(keys.size() > degree){
                        // 分裂当前界面
                        split();
                    }
                } else {
                    // 去当前界面的第 leI 个节点的子界面插入数据, 当前界面设为: nodePage.children[leI], 执行第2步
                    children.get(leI).treeInsert(key, value);
                }
            }
        }
    }
}
