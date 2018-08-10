package site.syzk.dataflow.core

import java.util.concurrent.Executor

data class ExecutableOptions
internal constructor(
		val parallelismDegree: Int,
		val dispatcher: Executor?
)

fun executableOptions() =
		ExecutableOptions(Int.MAX_VALUE, null)

fun executableOptions(parallelismDegree: Int) =
		ExecutableOptions(parallelismDegree, null)

fun executableOptions(dispatcher: Executor) =
		ExecutableOptions(Int.MAX_VALUE, dispatcher)
