package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.TargetCore

class ActionBlock<T>(
		override val name: String = "action",
		options: ExecutableOptions = executableOptions(),
		action: (T) -> Unit
) : ITarget<T> {
	override val defaultSource = DefaultSource(this)
	
	private val core = TargetCore(options, action)
	
	override fun offer(id: Long, link: Link<T>) = core.offer(id, link)
}