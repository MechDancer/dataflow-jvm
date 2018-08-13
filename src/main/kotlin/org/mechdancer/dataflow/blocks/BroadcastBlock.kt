package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.LinkManager
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.zip
import java.util.concurrent.atomic.AtomicLong

/**
 * 广播节点
 * 堆中的事件只会被新事件顶替，不会因为接收而消耗
 */
class BroadcastBlock<T>(override val name: String = "broadcast")
	: IPropagatorBlock<T, T>, IReceivable<T> {
	override val defaultSource = org.mechdancer.dataflow.core.DefaultSource(this)

	/**
	 * 存储已链接的节点
	 */
	private val manager = LinkManager(this)

	/**
	 * 唯一Id分配器
	 */
	private val id = AtomicLong(0)

	/**
	 * 堆
	 */
	private val buffer = hashMapOf<Long, T>()

	//--------------------------
	// IReceivable
	//--------------------------
	private val receiveLock = Object()
	private var receivable = false
	private var value: T? = null

	/**
	 * 作为目的节点的内核
	 * 新到来的事件顶替旧事件，然后向所有目的节点通报事件到来
	 */
	private val targetCore = TargetCore<T> { event ->
		val newId = id.incrementAndGet()
		synchronized(buffer) {
			buffer.clear()
			buffer[newId] = event
		}
		manager.links
				.filter { it.options.predicate(event) }
				.forEach { it.target.offer(newId, it) }
		synchronized(receiveLock) {
			receivable = true
			value = event
			receiveLock.notifyAll()
		}
	}

	override fun offer(id: Long, link: Link<T>) = targetCore.offer(id, link)
	override fun consume(id: Long, link: Link<T>): Pair<Boolean, T?> =
			synchronized(buffer) {
				buffer.containsKey(id).zip {
					link.record()
					buffer[id]
				}
			}

	override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) = manager.build(target, options)
	override fun unlink(link: Link<T>) = manager.cancel(link)

	override fun receive(): T =
			synchronized(receiveLock) {
				while (!receivable) receiveLock.wait()
				@Suppress("UNCHECKED_CAST")
				value as T
			}

}
