package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.LinkManager
import org.mechdancer.dataflow.core.internal.ReceiveCore
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import java.util.*

/**
 * 缓冲模块
 * 未消耗的数据将保留，直到被消费
 */
class BufferBlock<T>(
	override val name: String = "buffer",
	size: Int = Int.MAX_VALUE)
	: IPropagatorBlock<T, T>, IReceivable<T>, IPostable<T> {
	private val linkManager = LinkManager(this)
	private val receiveCore = ReceiveCore()
	private val sourceCore = SourceCore<T>(size)
	private val targetCore = TargetCore<T> { event ->
		linkManager[sourceCore.offer(event), event]
		receiveCore.call()
	}

	override val uuid = UUID.randomUUID()!!
	override val defaultSource by lazy { DefaultSource(this) }
	override val targets get() = linkManager.targets

	val count get() = sourceCore.bufferCount
	fun clear() = sourceCore.clear()

	override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
	override fun consume(id: Long) = sourceCore consume id
	override fun receive() = receiveCore consumeFrom sourceCore
	override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
		linkManager.linkTo(target, options)
}
