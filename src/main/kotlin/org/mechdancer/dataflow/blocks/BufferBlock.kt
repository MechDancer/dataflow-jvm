package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.otherwise
import java.util.*

/**
 * 缓冲模块
 * 未消耗的数据将保留，直到被消费
 */
class BufferBlock<T>(override val name: String = "buffer")
	: IPropagatorBlock<T, T>, IReceivable<T> {
	override val uuid = UUID.randomUUID()!!
	override val defaultSource = DefaultSource(this)

	private val receiveLock = Object()
	private val sourceCore = SourceCore<T>()
	private val targetCore = TargetCore<T> { event ->
		val newId = sourceCore.offer(event)
		@Suppress("UNCHECKED_CAST")
		Link.view()
				.filter { it.source == this }
				.map { it as Link<T> }
				.filter { it.options.predicate(event) }
				.any { it.target.offer(newId, it).positive }
				.otherwise { synchronized(receiveLock) { receiveLock.notifyAll() } }
	}

	val count get() = sourceCore.bufferCount

	override fun offer(id: Long, link: Link<T>) = targetCore.offer(id, link)
	override fun consume(id: Long, link: Link<T>) =
			sourceCore.consume(id).apply { if (this.first) link.record() }

	override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
			Link(this, target, options)

	override fun receive(): T {
		synchronized(receiveLock) {
			var pair = sourceCore.consume()
			while (!pair.first) {
				receiveLock.wait()
				pair = sourceCore.consume()
			}
			@Suppress("UNCHECKED_CAST")
			return pair.second as T
		}
	}
}
