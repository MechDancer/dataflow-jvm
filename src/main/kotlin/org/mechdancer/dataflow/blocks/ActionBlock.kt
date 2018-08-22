package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.DefaultSource
import org.mechdancer.dataflow.core.ExecutableOptions
import org.mechdancer.dataflow.core.ITarget
import org.mechdancer.dataflow.core.Link
import org.mechdancer.dataflow.core.internal.TargetCore
import java.util.*

class ActionBlock<T>(
        override val name: String = "action",
        options: ExecutableOptions = ExecutableOptions(),
        action: (T) -> Unit
) : ITarget<T> {
    override val uuid: UUID = UUID.randomUUID()
    override val defaultSource by lazy { DefaultSource(this) }
    private val core = TargetCore(options, action)

    override fun offer(id: Long, link: Link<T>) = core.offer(id, link)
}
