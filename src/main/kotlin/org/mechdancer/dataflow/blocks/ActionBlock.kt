package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.TargetCore
import java.util.*

class ActionBlock<T>(
	override val name: String = "action",
	options: ExecutableOptions = executableOptions(),
	action: (T) -> Unit
) : ITarget<T> {
	override val uuid = UUID.randomUUID()!!
	override val defaultSource = DefaultSource(this)
	override val snapshot get() = core.snapshot
	private val core = TargetCore(options, action)

	override fun offer(id: Long, link: Link<T>) = core.offer(id, link)
}
