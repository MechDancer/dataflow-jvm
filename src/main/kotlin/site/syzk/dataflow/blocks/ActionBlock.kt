package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.DefaultSource
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.TargetCore

class ActionBlock<T>(action: (T) -> Unit) : ITarget<T> {
    override fun offer(eventId: Long, source: ISource<T>) =
            core.offer(eventId, source)

    override val defaultSource = DefaultSource<T>()

    private val core = TargetCore(action)
}
