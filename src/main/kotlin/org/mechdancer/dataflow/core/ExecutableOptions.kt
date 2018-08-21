package org.mechdancer.dataflow.core

import java.util.concurrent.Executor

data class ExecutableOptions
internal constructor(
        val parallelismDegree: Int,
        val executor: Executor?
)

fun executableOptions() =
        ExecutableOptions(Int.MAX_VALUE, null)

fun executableOptions(parallelismDegree: Int) =
        ExecutableOptions(parallelismDegree, null)

fun executableOptions(executor: Executor) =
        ExecutableOptions(Int.MAX_VALUE, executor)
