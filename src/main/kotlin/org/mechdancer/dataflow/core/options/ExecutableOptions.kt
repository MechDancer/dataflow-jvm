package org.mechdancer.dataflow.core.options

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * 执行可选项
 *
 * @param parallelismDegree 并行度（最大可提交多少任务）
 * @param queueSize         队列容量（最多允许多少任务排队）
 * @param executor          调度器
 */
data class ExecutableOptions(
    val parallelismDegree: Int = 2,
    val queueSize: Int = Int.MAX_VALUE,
    val executor: CoroutineDispatcher = Dispatchers.Default
)
