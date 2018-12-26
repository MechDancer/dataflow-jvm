package org.mechdancer.dataflow.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * 执行可选项
 * @param parallelismDegree 并行度（最大可提交多少任务）
 * @param executor          调度器
 */
data class ExecutableOptions(
        val parallelismDegree: Int = 2,
        val executor: CoroutineDispatcher = Dispatchers.Default
)
