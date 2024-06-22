# V3版本
> - 源码位置: src/main/com/BPlusTree/V3
> - 测试文件位置: test/java/com/BPlusTree/V3

## 特点

| 版本  | 插入  | 删除  | 查找  | 修改  | 持久化 |
|-----|-----|-----|-----|-----|-----|
| V3  | √   | √   | √   | √   | ×   |

- 节点页使用链表的形式
  > 由于官方库的链表不能直接操作节点, 且不有序, 相当于每次都要从头节点开始, 效率很低, 所以这里自己实现了一个有序链表
  > : src/main/java/com/BPlusTree/SortedLinkList
- 数据节点用有序链表串起来 (V2版本是使用的普通链表串联节点页)
- 包含普通索引和唯一索引两种方式

## 结构

- 一个父节点对应一个子页面(子节点集合), 父节点的值是子节点里的最大值
- 节点页使用链表的形式
- 数据节点用有序链表串起来

![img.png](./files/V3.结构.png)

## 算法

### 查找

在当前页开始查找索引为 key 对应的节点 leNode
1. 查找当前页第一个 >=key 的节点
  > 如果没找到, 直接返回 null
2. 如果当前页不为叶子节点, 在子页 leNode.children 页继续查找
3. 为叶子节点, 如果 leNode.key != key, 没有满足要求的节点, 返回 null
4. 如果为唯一索引, 只存在一个满足要求的值, 直接返回 leNode.data
5. 不为唯一索引, 可能存在多个满足要求的值, 从 leNode.leafTreeNode 开始在叶子节点链表查找所有索引为 key 的值

### 更新

与查找的思路是一样的, 查找是查找满足要求的节点, 更新里找到这些节点后, 把这些节点的值更新成新值就可以了

### 插入

在当前页开始尝试插入键值对 [key: value]
1. 在当前页查找第一个 >= key 的节点 leNode
2. 如果为叶子节点
   1. 如果 leNode 为 null, 表示 key 是最大值
      1. 维护叶子链表: 在当前页最后一个索引节点后面添加新叶子节点
         > 如果当前页没有索引, 当且仅当什么也没有时才可能存在这种情况,此时叶子链表也为空, 直接尾部添加一个叶子节点即可
      2. 当前页在末尾插入 [key: value]
   2. leNode不为null
      1. 维护叶子链表: 在 leNode 对于的叶子节点前面添加新叶子节点
      2. 当前页在 leNode 前面插入新节点
   3. 如果当前页节点个数超出阶数, 分裂当前节点(见分裂算法)
3. 不为叶子节点
   1. 如果 leNode 为 null, 表示 key 是最大值
      1. 更新最大值(即最后一个节点)的索引为 key
      2. 在最后一个节点的子节点页 尝试插入 [key:value]
   2. leNode不为null, 在子节点页 leNode.children 尝试插入新节点

### 删除

在当前页开始尝试删除键 key 对应的值
1. 在当前页查找第一个索引 >= key 的节点 leNode
2. 如果 leNode 为 null, 没找到, 直接 return
3. 如果当前页为叶子页
   1. 判断 leNode.key == key, 不满足 return
   2. 从 leNode 开始删除索引为 key 的节点 eNode
      1. 维护叶子链表: 叶子链表里直接删除这个节点
      2. 删除 eNode, 如果当前页节点个数小于 m/2, 先尝试去右边要一个节点, 如果右边也不够, 就和右边合并成一个界面, 见合并算法
          > 如果当前页没有右边页, 去左边借
4. 如果当前页不为叶子页, 去子页 leNode.children 继续尝试删除


### 分裂算法

将当前页分成左右两个界面
> 条件: 页面的阶数 > 阶数
1. 为根页, 分裂成两个子页, 根页面设置两个字页索引
2. 不为根页, 分裂成两个页, 父页面新添加一个索引, 如果父页面节点数也超出阶数, 继续分裂父页面

### 拓展 |合并算法

将当前页拓展到节点个数 >= 阶数/2
> 条件:
> 1. 两个页面的阶数和 <= 阶数
> 2. 两个页面的父页面相同

## 父子节点关系

current 表示当前页, parent 表示父页
1. current.parentPage = parent, 
2. current.parentKeyNode in parent.nodes // 索引
3. current.parentKeyTreeNode.children = parent // 索引节点子页面
4. 对于 current.nodes: 
   - 如果 current 不是叶子, node.children -> current 是 node.children 的父界面, 对 node.children 进行第 1, 2, 3步
   - node.page = current // 所属页
   - node.keyListNode in current.nodes // 索引链表节点