# 基于 Java 实现的 B+ 树
> 参考文献:
> - [B+树详解](https://ivanzz1001.github.io/records/post/data-structure/2018/06/16/ds-bplustree)
> - [MySQL B+树相对于B树的区别及优势](https://juejin.cn/post/7117516433386373133)
> - [数据结构合集 - B+树](https://www.bilibili.com/video/BV1bs421u7pY/)

## 本项目实现了一些比较常用的 B+ 树, 共有以下几个版本

| 版本  | 说明                                      | 插入  | 删除  | 查找  | 修改  | 存文件 | 其他特点见文档                     |
| --- | --------------------------------------- | --- | --- | --- | --- | --- |-----------------------------|
| V1  | 简易版本, 只实现了部分功能, 实现方式也比较简单, 主要用于理论验证     | √   | ×   | √   | ×   | ×   | [V1版本文档](docs/README.V1.md) |
| V2  | 完备版本, 实现了几乎全部功能, 实现方式上考虑了各种场景, 对细节进行了优化 | √   | √   | √   | √   | ×   | [V2版本文档](docs/README.V2.md)      |
| V3  | 数据库定制版本, 实现将数据保存在文件中                    | √   | √   | √   | √   | √   | [V2版本文档](docs/README.V3.md)      |


