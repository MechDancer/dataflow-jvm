package org.mechdancer.dataflow.core

import java.util.concurrent.Executor

/**
 * 执行可选项
 * @param parallelismDegree 并行度（最大可提交多少任务）
 * @param executor          调度器
 */
data class ExecutableOptions(
    val parallelismDegree: Int = Int.MAX_VALUE,
    val executor: Executor? = null
)
