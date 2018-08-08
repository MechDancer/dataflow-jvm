package site.syzk.dataflow.core

import java.util.concurrent.ThreadPoolExecutor

data class ExecutableOptions
internal constructor(
        val parallelismDegree: Int,
        val dispatcher: ThreadPoolExecutor?
)

fun executableOptions() =
        ExecutableOptions(Int.MAX_VALUE, null)

fun executableOptions(parallelismDegree: Int) =
        ExecutableOptions(parallelismDegree, null)

fun executableOptions(dispatcher: ThreadPoolExecutor) =
        ExecutableOptions(Int.MAX_VALUE, dispatcher)
