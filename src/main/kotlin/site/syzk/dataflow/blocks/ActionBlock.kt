package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.DefaultSource
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.internal.TargetCore

class ActionBlock<T>(action: (T) -> Unit) : ITarget<T> {
    override val defaultSource = DefaultSource<T>()

    private val core = TargetCore(action)

    override fun offer(id: Long, source: ISource<T>) = core.offer(id, source)
}
