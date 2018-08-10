package site.syzk.dataflow.core.internal

import site.syzk.dataflow.annotations.ThreadSafe
import java.util.concurrent.atomic.AtomicLong

/**
 * 源节点的通用内核
 * 提供事件管理和基于散列的事件缓存
 */
@ThreadSafe
internal class SourceCore<T> {
	/**
	 * 原子长整型，用于生成唯一Id
	 */
	private val id = AtomicLong(0)

	/**
	 * 事件堆
	 */
	private val buffer = hashMapOf<Long, T>()

	/**
	 * 缓存存量
	 */
	val bufferCount get() = buffer.size

	/**
	 * 将一个事件放入堆
	 */
	fun offer(event: T): Long {
		val newId = id.incrementAndGet()
		buffer[newId] = event
		return newId
	}

	/**
	 * 从堆中消费一个事件
	 */
	fun consume(id: Long): Pair<Boolean, T?> {
		synchronized(buffer) {
			return buffer.containsKey(id)
					.zip { buffer.remove(id) }
		}
	}

	/**
	 * 从堆中消费第一个事件
	 */
	fun consume(): Pair<Boolean, T?> {
		synchronized(buffer) {
			return buffer.isNotEmpty()
					.zip { buffer.remove(buffer.keys.min()) }
		}
	}

	/**
	 * 从堆中丢弃一个事件
	 */
	fun drop(id: Long) {
		synchronized(buffer) { buffer.remove(id) }
	}
}
