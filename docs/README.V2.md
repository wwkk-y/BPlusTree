# V2版本 
> - 源码位置: src/main/com/BPlusTree/V2
> - 测试文件位置: test/java/com/BPlusTree/V2

## 特点

| 版本  | 插入  | 删除  | 查找  | 修改  | 持久化 |
| --- | --- | --- | --- | --- |-----|
| V2  | √   | √   | √   | √   | ×   |

- 唯一索引, key不能重复
- 节点页使用链表的形式
  > 由于官方库的链表不能直接操作节点, 且不有序, 相当于每次都要从头节点开始, 效率很低, 所以这里自己实现了一个有序链表 
  > > 源码: src/main/java/com/BPlusTree/EasyLinkList
- 数据页用链表串起来

## 结构

这里采取的策略是, 一个父节点对应一个子页面(子节点集合), 父节点的值是子节点里的最大值, 举个例子, 如下

![img.png](files/img.png)
> 父界面 parentPage 与子界面 childPage 对应关系:
> - childPage.parentPage = parentPage
>  ```java
>  childPage = new BPlusTreeNodePage<>(..., parentPage);
>  ```
> - parentPage.nodes 每一个元素都含有一个 children 表示子节点页 = childPage
> - parentPage.nodes 包含 childPage 最大索引值
> - childPage.parentListNode = parentPage.nodes 里对应索引值所在节点
>  ```java
>  parentKeyNode = new BPlusTreeNode<>(childPageMaxKey, childPage);
>  childPage.parentListNode = nodes.insert(childPageMaxKeyNode);
>  ``` 




## 算法

### 搜索
> 实现: BPlusTreeNodePage.treeSelect(K key)

在根页面 rootPage 开始查找 key 对应的值
1. 在当前页查找第一个 >= key 的树节点 leTreeNode, 没找到直接返回 NULL
2. 如果 leTreeNode 是叶子节点, 判断 key 相不相等
   1. true: 找到了, 返回对应值
   2. false: 不存在这个 key, 返回 NULL
3. leTreeNode不是叶子节点, 继续搜索 leTreeNode 的子节点页

### 分裂算法
> 将当前阶段页面分裂成两个相等的子页面
>
> 实现: BPlusTreeNodePage.trySplit()

要分裂的界面为 curPage
1. 如果为根页面(curPage.parentListNode == null)
   1. 将索引页均分成 left 和 right (快慢指针, 实现细节见 SortedLinkList.midSplit() )
   2. 节点页设置为两个节点, 分别为 left 和 right 的最大值
   3. 绑定 left 和 right 对应父子节点关系
   4. 当前界面设为非叶子
2. 不为根节点 
   1. 均分索引页为 left 和 right, curPage 为 left, 更新父节点索引值
   2. 在 left 索引的右边添加 right 的索引, 绑定 right 和父节点的关系
   3. 如果父页大小超出阶数, 分裂父页

### 插入
> 实现: BPlusTreeNodePage.treeInsert(K key, V value)

在根页面 rootPage 开始尝试插入 (key: value)
1. 在当前页查找第一个 >= key 的节点 leTreeNode
   1. 如果没有找到, 表示当前 key 为最大值, 判断当前页面是不是叶子
      1. true: 直接在当前页末尾插入新节点, 判断是否大于阶数, 大于就分裂当前页(见分裂算法)
      2. false: 更新最后的索引值为最大值 key, 在最后一个节点的子节点页插入节点
   2. 如果找到了, 判断当前界面是不是叶子
      1. true: 尝试在当前页插入新节点, 如果插入成功, 判断是否大于阶数, 大于就分裂当前页(见分裂算法)
      2. false: 在 leTreeNode 的子界面添加数据