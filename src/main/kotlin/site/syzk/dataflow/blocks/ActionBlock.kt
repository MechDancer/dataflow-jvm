package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.*
import site.syzk.dataflow.core.internal.TargetCore

class ActionBlock<T>(
        override val name: String,
        options: ExecutableOptions = executableOptions(),
        action: (T) -> Unit
) : ITarget<T> {
    override val defaultSource = DefaultSource(this)

    private val core = TargetCore(options, action)

    override fun offer(id: Long, link: Link<T>) = core.offer(id, link)
}
