# 基于 Java 实现的 B+ 树
> 参考文献:
> - [B+树详解](https://ivanzz1001.github.io/records/post/data-structure/2018/06/16/ds-bplustree)
> - [数据结构合集 - B+树](https://www.bilibili.com/video/BV1bs421u7pY/)
> - [MySQL B+树相对于B树的区别及优势](https://juejin.cn/post/7117516433386373133)
> - [mysql InnoDB有几个文件 mysql innodb存储的文件结构](https://blog.51cto.com/u_16099267/9567953)
> - [深入浅出索引（上）](https://time.geekbang.org/column/article/69236)
> - [深入浅出索引（下）](https://time.geekbang.org/column/article/69636)
> - [普通索引和唯一索引，应该怎么选择？](https://time.geekbang.org/column/article/70848)
> - [怎么给字符串字段加索引？](https://time.geekbang.org/column/article/71492)
> - [日志和索引相关问题](https://time.geekbang.org/column/article/73161)

## 运行环境
- Java
    - SDK: 1.8
    - 语言级别: 8
- Maven: 3.8.1

## 本项目实现了一些比较常用的 B+ 树, 共有以下几个版本

- V1: 简化版本, 只实现了部分功能, 实现方式也比较简单, 主要用于理论验证
- V2: 完备版本, 实现了几乎全部功能, 实现方式上考虑了各种场景, 对细节进行了优化
- V3: 数据库定制版本, 实现将数据持久化, 实现上考虑了数据库的各种场景

| 版本  | 插入  | 删除  | 查找  | 修改  | 存文件 | 其他特点见文档                |
| --- | --- | --- | --- | --- | --- | ---------------------- |
| V1  | √   | ×   | √   | ×   | ×   | [V1版本文档](README.V1.md) |
| V2  | √   | √   | √   | √   | ×   | [V2版本文档](README.V2.md) |
| V3  | √   | √   | √   | √   | √   | [V3版本文档](README.V3.md) |

## 生态
- 基于此项目实现的关系型数据库: [EasySQL](https://github.com/wwkk-y/EasySQL)