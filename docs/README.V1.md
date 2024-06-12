# V1版本 
> - 源码位置: src/main/com/BPlusTree/V1
> - 测试文件位置: test/java/com/BPlusTree/V1

## 特点
- 插入: √
- 查找: √
- 删除: ×
- 修改: ×
- 存文件: ×
- 数据页使用数组来表示一组节点或者子节点
- 叶子节点和数据没有串起来, 遍历时只能使用中序遍历
- 只实现了索引 key 的泛型, 没有实现值 value 的泛型, 用的 Object

## 结构
> 对于唯一索引B+树, 不能包含重复的key

这里采取的策略是, 一个父节点对应一个子页面(子节点集合), 父节点的值是子节点里的最大值, 举个例子, 如下

```text
           +-----------+-----------+-----------+
           |     3     |     7     |     8     |
           +-----------+-----------+-----------+
               /              |         /
+-----+-----+-----+  +-----+-----+   +-----+
|  1  |  2  |  3  |  |  5  |  7  |   |  8  |
+-----+-----+-----+  +-----+-----+   +-----+
| dat | dat | dat |  | dat | dat |   | dat |
+-----+-----+-----+  +-----+-----+   +-----+
```

## 算法

### 搜索
> 具体实现:
> - BPlusTree.searchPage(T key) // 查找 key 所在数据页
> - BPlusTree.searchValue(T key) // 查找 key 位置的数据

1. 当前页面 nodePage 设为 rootPage, 从根页面开始搜索关键字 key
2. 在当前页面已有 keys 里找第一个 >= key 的位置 leI (large equal index)
    > 由于 keys 是有序的, 可以采取二分查找, 当然这里数据量比较小, 直接顺序搜索性能也可能更好
3. 判断 leI 是否超出当前页面已有索引 keys 的范围
   - true: 表示当前页面最大的节点索引都没有 key 大, 即要找的元素不存在
   - false: 判断当前页面是不是叶子页面
     - true: 判断 key == keys[leI]
        - true: 表示找到了对应元素, 根据需求返回数据页或者节点
        - false: 表示要找的元素不存在
     - false: 表示 key 在当前页面第 leI 个节点的范围里, 继续去子页面里搜索, 当前页面 nodePage 设为 nodePage.children[leI], 执行第2步

### 插入
> 这里实现的是唯一索引, 对于已经存在的key, 如果插入相同的值, 这里采取的措施是抛出异常
> 
> 具体实现: BPlusTree.insert(T key, Object value)

1. 当前界面 nodePage 设为 rootPage, 从根界面开始尝试插入数据(key, value)
2. 在当前页面已有 keys 里找第一个 >= key 的位置 leI (large equal index)
3. 判断 leI 是否超出当前页面已有索引 keys 的范围
   - true: 表示要插入的数据比所有数据都要大, 判断当前界面是不是叶子
     - true: 
       1. 在末尾位置插入数据,
       2. 更新父节点(如果有)里的索引值为当前值(保证是最大值), 循环往上, 直到没有父节点或者不是最后一个孩子
       3. 判断当前界面 keys 的大小超过阶数没有(超过允许的最大值)
         - true: 分裂当前界面, 执行分裂算法(具体思路在后面)
     - false: 直接去最后一个子节点里尝试插入数据, 当前界面设为 nodePage.children[nodePage.children - 1], 执行第2步
   - false: 判断 keys[leI] == key
     - true: 表示键已经存在了, 抛出异常
     - false: 判断当前界面是不是叶子
       - true: 在 leI 位置插入数据, 判断当前界面 keys 的大小超过阶数没有(超过允许的最大值), 如果超过了, 执行分裂算法
       - false: 去当前界面的第 leI 个节点的子界面插入数据, 当前界面设为: nodePage.children[leI], 执行第2步

#### 分裂算法
> 对于一个已经满的界面(索引个数 = 阶数 或者 阶数+1), 将他分裂成两个界面
> 
> 具体实现: BPlusTreeNodePage.split()

假设要分裂的界面是 nodePage, 并且阶数为 degree
1. 判断当前界面是不是根界面 (nodePage.parent == null)
   1. true: 
      1. 根界面对半分成的两个子界面 left 和 right, 
      2. 绑定根界面和子界面
      3. 根界面设置成非叶子
   2. false: 
      1. nodePage 把右半边的数据新生成一个界面 right
      2. 更新 nodePage 和其在父节点里的索引值
      3. 在父节点 nodePage 索引值右边插入 right 索引值
      4. 绑定父节点和 right, 插入时注意更新后面被影响的节点记录的位置信息
      5. 判断父节点的索引值个数超了没有, 如果超了, 对父节点继续执行分裂算法
> 需要注意的是对半分时, 不止对半分 keys, 还有 children, data 也要分到对应的界面, 而且子节点的 parent 以及 indexInParent 也要更新