# 计算图模型的JVM实现

此项目模仿微软的[数据流（任务并行库）](https://docs.microsoft.com/zh-cn/dotnet/standard/parallel-programming/dataflow-task-parallel-library?view=netcore-2.1)，目标是在Java虚拟机实现可用于机器人系统的并行计算网络。

项目采用将从最简单的可用系统开始，逐步优化并添加功能。

## 任务

- [x] 定义最基本的两个接口（源、目的）
- [x] 实现最基本的四个通用模块（动作、广播、缓冲、转换）
