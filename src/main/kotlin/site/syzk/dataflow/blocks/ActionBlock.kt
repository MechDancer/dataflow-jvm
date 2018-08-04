package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ITarget

class ActionBlock<T>(private val action: (T) -> Unit) : ITarget<T> {
    override fun consume(event: T) = action(event)
}
