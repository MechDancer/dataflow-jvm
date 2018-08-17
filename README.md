# 计算图模型的JVM实现

[![Download](https://api.bintray.com/packages/mechdancer/maven/dataflow-jvm/images/download.svg) ](https://bintray.com/mechdancer/maven/dataflow-jvm/_latestVersion) [![Build Status](https://www.travis-ci.org/MechDancer/dataflow-jvm.svg?branch=master)](https://www.travis-ci.org/MechDancer/dataflow-jvm)

此项目模仿微软的 [数据流（任务并行库）](https://docs.microsoft.com/zh-cn/dotnet/standard/parallel-programming/dataflow-task-parallel-library?view=netcore-2.1)，目标是在Java虚拟机实现可用于机器人系统的并行计算网络。

项目采用将从最简单的可用系统开始，逐步优化并添加功能。

## 路线图

根据对.Net Core源码的研究，本代码库的本质仍是经典的生产者、消费者模型。数据流的特征体现在用响应式修饰内核模型。因此，整个库的实现将包含5个主要的功能模块：

1. 内核模型（生产者-消费者）
2. 响应模型（事件和通知）
3. 调度模型（默认基于并行度调度、插入调度器调度）
4. 管理模型（分析和检查拓扑结构，计算各模块的资源占用）
5. 解耦合模型（允许通过配置文件管理用户拓扑） 

## 任务

- [x] 定义最基本的两个接口（源、目的）
- [x] 基于内核模型和响应模型实现最基本的四个通用模块（动作、广播、缓冲、转换）
- [x] 补充拓扑管理功能，包括消息计数、消息过滤和链接取消
- [x] 实现内置的基于并行度调度，使模块可反馈出“推迟”
- [x] 实现执行块在外部调度器中调度
- [x] 输出当前拓扑结构
- [x] 允许订阅拓扑动态
- [x] 拓扑分析算法
- [x] 输出块运行情况
- [x] 添加DSL，帮助用户快速定义拓扑
- [ ] 采用数据交换格式，定义解耦合模型
- [ ] 添加对分布式拓扑的支持
* 写文档

## 开始使用

* Gradle
* Maven
* Bintray

您需要将其添加至  [仓库和依赖](https://docs.gradle.org/current/userguide/declaring_dependencies.html) 中。

### Gradle

```groovy
repositories {
    jcenter()
}
dependencies {
    compile 'org.mechdancer:dataflow-jvm:0.1.0'
}
```

### Maven

```xml
<repositories>
   <repository>
     <id>jcenter</id>
     <name>JCenter</name>
     <url>https://jcenter.bintray.com/</url>
   </repository>
</repositories>

<dependency>
  <groupId>org.mechdancer</groupId>
  <artifactId>dataflow-jvm</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```

### Bintray

您总可以从 bintray 直接下载 jar：[![Download](https://api.bintray.com/packages/mechdancer/maven/dataflow-jvm/images/download.svg) ](https://bintray.com/mechdancer/maven/dataflow-jvm/_latestVersion)

## 使用说明

### 1. 使用标准节点

库现在提供四种标准节点：

* 广播节点 BroadcastBlock
* 缓冲节点 BufferBlock
* 变换节点 TransformBlock
* 动作节点 ActionBlock

建议使用简写函数构造节点，可以在构造时指定名字，也可以使用默认的名字：

```kotlin
val source = broadcast<Int>("源")
val buffer = buffer<Int>()
val bridge = transform { x: Int -> x - 1 }
val action = action { x: Int -> println(x) }
```

对于执行用户函数的节点（变换、动作），可以传入执行选项，在执行选项中指定最大并行度调度器：

```kotlin
val bridge1 = transform(options = executableOptions(1)) { x: Int -> x - 1 }
```

### 2.链接拓扑

你可以使用多种风格构造拓扑，大部分会返回链接的引用，你可以保存引用以便在需要时取消链接。有的方法还允许你传入链接选项：

```kotlin
val link = link(source, bridge1) { it > 100 }
link(source, bridge2, linkOptions(100))
source - { it > 0 } - { println(if (it) "+" else "-") }
bridge1 linkTo source
bridge2 linkTo source
source linkTo { println(link.eventCount / (System.currentTimeMillis() - begin)) }
```

**构建拓扑时不建议包含环路。**

### 3. 拓扑分析

你可以在任何时候获取当前存在的所有链接，或用树状图查看从一个源出发的链接关系。

```kotlin
Link.view().forEach { println(it) }
println(source.treeView())
```

```
broadcast[230bbab9-a942-4ea8-9dc3-3b30e1c9d724]
 ├─action[de29b385-be3e-42d9-8d57-3e7355ee3239]
 ├─transform[6543bda7-780f-4948-af7e-e96d12541521]
 │  └─transform[0bed1e42-fa4c-49a3-91b3-b92c490d0652]
 ├─transform[309987da-7c49-43df-b753-7b91e5bdbd80]
 │  └─broadcast[230bbab9-a942-4ea8-9dc3-3b30e1c9d724][Loop!!!]
 └─transform[895491f4-b9e6-4862-975b-b60b7d19a6b0]
    └─broadcast[230bbab9-a942-4ea8-9dc3-3b30e1c9d724][Loop!!!]
```

通过订阅拓扑改变事件，你也可以在第一时间获悉拓扑动态。

```kotlin
val lock = Object()
Link.changed linkTo { list ->
	synchronized(lock) {
		println(list.size)
		list.forEach { println(it) }
		println()
	}
}
```

### 4.尽情享受

构造你自己的网络，享受数据流的简洁高效吧！
