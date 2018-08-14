# 计算图模型的JVM实现

[![Download](https://api.bintray.com/packages/mechdancer/maven/dataflow-jvm/images/download.svg) ](https://bintray.com/mechdancer/maven/dataflow-jvm/_latestVersion)

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
- [x] 实现encapsulate函数，允许用户自定义数据流块
- [x] 输出当前拓扑结构
- [ ] 允许订阅拓扑动态
- [ ] 拓扑分析算法
- [ ] 输出块运行情况
- [ ] 添加DSL，允许用户快速定义拓扑
- [ ] 采用数据交换格式定义解耦合模型
- [ ] 添加对分布式拓扑的支持
- [ ] 写文档

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

