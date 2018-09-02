package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.*
import java.util.*
import java.util.concurrent.TimeUnit

class DelayBlock<T>(
	override val name: String = "delay",
	delay: Long,
	unit: TimeUnit)
	: IBridgeBlock<T>, IReceivable<T>, IPostable<T> {
	private val linkManager = LinkManager(this)
	private val receiveCore = ReceiveCore()
	private val sourceCore = SourceCore<T>(Int.MAX_VALUE)
	private val targetCore = TargetCore<T> { event ->
		scheduler.schedule({
			linkManager[sourceCore.offer(event), event]
			receiveCore.call()
		}, delay, unit)
	}

	override val uuid = UUID.randomUUID()!!
	override val defaultSource by lazy { DefaultSource(this) }
	override val targets get() = linkManager.targets

	override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
	override fun consume(id: Long) = sourceCore consume id
	override fun receive() = receiveCore consumeFrom sourceCore

	override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
		linkManager.linkTo(target, options)
}
