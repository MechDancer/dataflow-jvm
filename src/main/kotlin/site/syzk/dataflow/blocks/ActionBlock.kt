package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.DefaultSource
import site.syzk.dataflow.core.ExecutableOptions
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.Link
import site.syzk.dataflow.core.internal.TargetCore

class ActionBlock<T>(
        override val name: String,
        executableOptions: ExecutableOptions =
                ExecutableOptions(Int.MAX_VALUE, null),
        action: (T) -> Unit
) : ITarget<T> {
    override val defaultSource = DefaultSource(this)

    private val core = TargetCore(executableOptions, action)

    override fun offer(id: Long, link: Link<T>) = core.offer(id, link)
}
