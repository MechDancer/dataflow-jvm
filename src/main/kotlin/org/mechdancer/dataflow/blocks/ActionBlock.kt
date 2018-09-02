package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.DefaultSource
import org.mechdancer.dataflow.core.ExecutableOptions
import org.mechdancer.dataflow.core.IEgress
import org.mechdancer.dataflow.core.ITarget
import org.mechdancer.dataflow.core.internal.TargetCore
import java.util.*

/** 动作节点 */
class ActionBlock<T>(
    override val name: String = "action",
    options: ExecutableOptions = ExecutableOptions(),
    action: (T) -> Unit
) : ITarget<T> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }
    private val core = TargetCore(options, action)
	override fun offer(id: Long, egress: IEgress<T>) = core.offer(id, egress)
}
