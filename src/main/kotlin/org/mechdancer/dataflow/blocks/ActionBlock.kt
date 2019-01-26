package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.ExecutableOptions
import org.mechdancer.dataflow.core.IEgress
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.randomUUID
import org.mechdancer.dataflow.core.internal.view

class ActionBlock<T>(
        override val name: String = "action",
        options: ExecutableOptions = ExecutableOptions(),
        action: suspend (T) -> Unit
) : IActionBlock<T> {
    override val uuid = randomUUID()
    override val defaultSource by lazy { DefaultSource(this) }
    private val core = TargetCore(options, action)
    override fun offer(id: Long, egress: IEgress<T>) = core.offer(id, egress)
    override fun toString() = view()
}
