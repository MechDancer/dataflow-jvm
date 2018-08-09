package site.syzk.dataflow.core.internal

import site.syzk.dataflow.blocks.ActionBlock
import site.syzk.dataflow.blocks.BroadcastBlock
import site.syzk.dataflow.blocks.BufferBlock
import site.syzk.dataflow.blocks.TransformBlock
import site.syzk.dataflow.core.ExecutableOptions
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.executableOptions

infix fun <T> ITarget<T>.post(event: T) =
        defaultSource.offer(event).let { offer(it.first, it.second) }

operator fun <T> ISource<T>.minus(target: ITarget<T>) =
        linkTo(target)

operator fun <T> ISource<T>.minus(target: (T) -> Unit) =
        linkTo(ActionBlock(action = target))

fun <T> action(
        name: String = "action",
        options: ExecutableOptions = executableOptions(),
        action: (T) -> Unit
) = ActionBlock(name, options, action)

fun <T> broadcast(name: String = "broadcast") = BroadcastBlock<T>(name)

fun <T> buffer(name: String = "buffer") = BufferBlock<T>(name)

fun <TIn, TOut> transform(
        name: String = "transform",
        options: ExecutableOptions = executableOptions(),
        map: (TIn) -> TOut
) = TransformBlock(name, options, map)

internal fun Boolean.then(block: () -> Unit): Boolean {
    if (this) block()
    return this
}

internal fun Boolean.otherwise(block: () -> Unit): Boolean {
    if (!this) block()
    return this
}
