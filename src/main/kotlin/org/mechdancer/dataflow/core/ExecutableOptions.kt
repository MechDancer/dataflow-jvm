package org.mechdancer.dataflow.core

import java.util.concurrent.Executor

data class ExecutableOptions(
    val parallelismDegree: Int = Int.MAX_VALUE,
    val executor: Executor? = null
)
