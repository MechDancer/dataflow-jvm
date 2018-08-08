package site.syzk.dataflow.core

import java.util.concurrent.ThreadPoolExecutor

data class ExecutableOptions
internal constructor(
        val parallelismDegree: Int,
        val dispatcher: ThreadPoolExecutor?
)

fun executableOpotions() =
        ExecutableOptions(Int.MAX_VALUE, null)

fun executableOpotions(parallelismDegree: Int) =
        ExecutableOptions(parallelismDegree, null)

fun executableOpotions(dispatcher: ThreadPoolExecutor) =
        ExecutableOptions(Int.MAX_VALUE, dispatcher)
