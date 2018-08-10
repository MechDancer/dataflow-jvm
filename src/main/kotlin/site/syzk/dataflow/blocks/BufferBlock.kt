package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.*
import site.syzk.dataflow.core.internal.LinkManager
import site.syzk.dataflow.core.internal.SourceCore
import site.syzk.dataflow.core.internal.TargetCore
import site.syzk.dataflow.core.internal.otherwise

/**
 * 缓冲模块
 * 未消耗的数据将保留，直到被消费
 */
class BufferBlock<T>(override val name: String = "buffer")
	: ITarget<T>, ISource<T>, IReceivable<T> {
	override val defaultSource = DefaultSource(this)

	private val manager = LinkManager(this)
	private val receiveLock = Object()
	private val sourceCore = SourceCore<T>()
	private val targetCore = TargetCore<T> { event ->
		val newId = sourceCore.offer(event)
		manager.links
				.filter { it.options.predicate(event) }
				.any { it.target.offer(newId, it).positive }
				.otherwise { synchronized(receiveLock) { receiveLock.notifyAll() } }
	}

	val count get() = sourceCore.bufferCount

	override fun offer(id: Long, link: Link<T>) = targetCore.offer(id, link)
	override fun consume(id: Long, link: Link<T>) = sourceCore.consume(id).apply { if (this.first) link.record() }

	override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) = manager.build(target, options)
	override fun unlink(link: Link<T>) = manager.cancel(link)

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
